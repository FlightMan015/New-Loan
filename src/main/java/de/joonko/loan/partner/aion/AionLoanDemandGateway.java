package de.joonko.loan.partner.aion;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.dac.fts.model.FtsRawData;
import de.joonko.loan.db.repositories.LoanDemandRequestRepository;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.metric.model.Process;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.aion.mapper.AionLoanProviderApiMapper;
import de.joonko.loan.partner.aion.model.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class AionLoanDemandGateway implements LoanDemandGateway<AionLoanProviderApiMapper, CreditApplicationRequest, CreditApplicationResponse> {

    private final AionLoanProviderApiMapper aionLoanProviderApiMapper;

    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    private final AionClient aionClient;

    private final AionPrecheckFilter precheckFilter;

    private final AionStoreService aionStoreService;

    private final LoanDemandRequestRepository loanDemandRequestRepository;

    private final FTSAccountSnapshotGateway ftsAccountSnapshotGateway;

    @Override
    public Process getCallApiProcessName() {
        return Process.PROCESS_INITIAL_DATA;
    }

    @Override
    public AionLoanProviderApiMapper getMapper() {
        return aionLoanProviderApiMapper;
    }

    @Override
    public Mono<CreditApplicationResponse> callApi(final CreditApplicationRequest creditApplicationRequest, final String id) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(id, Bank.AION);
        log.info("Requesting to Aion for applicationId {}", id);

        return aionClient.getToken(id)
                .zipWhen(token -> fetchFtsDataAndSetInRequest(creditApplicationRequest, id))
                .flatMap(tuple -> aionClient.processInitialData(tuple.getT1(), tuple.getT2(), id))
                .map(response -> mapResponseToStore(response, id))
                .flatMap(aionStoreService::saveCreditApplicationResponse)
                .map(this::mapStoreToResponse);
    }

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.AION.getLabel()).build();
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) throws RemoteException {
        return !precheckFilter.test(loanDemand);
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        return List.of(LoanDuration.TWELVE);
    }

    public Flux<LoanOffer> getOffers(final String applicationId, final String processId, final BestOffersRequest[] bestOffersRequest) {
        log.info("Sending offers to beat to Aion for applicationId {}, request - {}", applicationId, bestOffersRequest);

        return processOffersToBeat(bestOffersRequest, applicationId, processId)
                .map(this::mapBestOfferResponseToStore)
                .doOnNext(offers -> log.info("AION: Received {} number of offers from offers to beat endpoint for processId - {}", offers.size(), processId))
                .flatMap(offers -> aionStoreService.addOffersProvided(applicationId, offers))
                .map(this::mapToOffers)
                .flatMapMany(Flux::fromIterable);
    }

    private List<LoanOffer> mapToOffers(final CreditApplicationResponseStore responseStore) {
        return responseStore.getOffersProvided().stream()
                .map(this::mapBestOfferToLoanOffer)
                .collect(toList());
    }

    private LoanOffer mapBestOfferToLoanOffer(final BestOfferValue bestOfferValue) {
        return LoanOffer.builder()
                .amount(bestOfferValue.getOfferDetails().getAmount().intValue())
                .durationInMonth(bestOfferValue.getOfferDetails().getMaturity())
                .monthlyRate(bestOfferValue.getOfferDetails().getMonthlyInstalmentAmount())
                .totalPayment(bestOfferValue.getOfferDetails().getTotalRepaymentAmount())
                .effectiveInterestRate(bestOfferValue.getOfferDetails().getAnnualPercentageRate().multiply(BigDecimal.valueOf(100)))
                .nominalInterestRate(bestOfferValue.getOfferDetails().getNominalInterestRate().multiply(BigDecimal.valueOf(100)))
                .loanProvider(LoanProvider.builder().name(Bank.AION.label).build())
                .loanProviderOfferId(bestOfferValue.getOfferDetails().getId())
                .build();
    }

    private Mono<CreditApplicationRequest> fetchFtsDataAndSetInRequest(CreditApplicationRequest creditApplicationRequest, String id) {
        return getLoanDemandStore(id)
                .map(LoanDemandRequest::getFtsTransactionId)
                .doOnNext(transactionId -> log.info("AION : Fetching accountSnapshot for transactionId {} ", transactionId))
                .flatMap(ftsAccountSnapshotGateway::fetchAccountSnapshotJson)
                .map(ftsRawData -> setPsd2RawDataInRequest(creditApplicationRequest, ftsRawData));
    }

    private CreditApplicationRequest setPsd2RawDataInRequest(final CreditApplicationRequest creditApplicationRequest, final FtsRawData ftsRawData) {
        List<CreditApplicationRequest.Variable> collect = creditApplicationRequest.getVariables()
                .stream()
                .map(variable -> {
                    if (TransmissionDataType.PSD2_RAW.equals(variable.getTransmissionDataType())) {
                        variable.setTransmissionData(PSD2RawDataTransmissionData.builder()
                                .account(ftsRawData.getAccount())
                                .balance(ftsRawData.getBalance())
                                .turnovers(ftsRawData.getTurnovers())
                                .date(ftsRawData.date)
                                .days(ftsRawData.days)
                                .filters(ftsRawData.filters)
                                .build());
                    }
                    return variable;
                }).collect(toList());
        creditApplicationRequest.setVariables(collect);
        return creditApplicationRequest;
    }

    protected Mono<LoanDemandRequest> getLoanDemandStore(String id) {
        return Mono.fromCallable(() -> loanDemandRequestRepository.findByApplicationId(id)
                        .orElseThrow(() -> new RuntimeException("applicationId not found " + id)))
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<OffersToBeatResponse> processOffersToBeat(final BestOffersRequest[] bestOffersRequestArray, final String applicationId, final String processId) {
        return aionClient.getToken(applicationId)
                .flatMap(token -> aionClient.processOffersToBeat(token, bestOffersRequestArray, applicationId, processId))
                .onErrorResume(err -> Mono.just(createEmptyResponse(processId)));
    }

    private OffersToBeatResponse createEmptyResponse(final String processId) {
        return OffersToBeatResponse.builder()
                .processId(processId)
                .variables(List.of(OffersToBeatResponse.Variable.builder()
                        .name(AionResponseValueType.OFFERS_LIST)
                        .value(List.of())
                        .build()))
                .build();
    }

    private CreditApplicationResponseStore mapResponseToStore(final CreditApplicationResponse creditApplicationResponse, final String applicationId) {
        return CreditApplicationResponseStore.builder()
                .applicationId(applicationId)
                .processId(creditApplicationResponse.getProcessId())
                .variables(creditApplicationResponse.getVariables().stream()
                        .map(v -> CreditApplicationResponseStore.Variable.builder()
                                .name(v.getName())
                                .value(v.getValue())
                                .build()).collect(toList()))
                .build();
    }


    private List<BestOfferValue> mapBestOfferResponseToStore(final OffersToBeatResponse offersToBeatResponse) {
        return offersToBeatResponse.getVariables()
                .stream().filter(v -> v.getName().equals(AionResponseValueType.OFFERS_LIST))
                .findFirst().map(OffersToBeatResponse.Variable::getValue).orElse(List.of());
    }

    private CreditApplicationResponse mapStoreToResponse(final CreditApplicationResponseStore store) {
        return CreditApplicationResponse.builder()
                .processId(store.getProcessId())
                .variables(store.getVariables().stream()
                        .map(v -> CreditApplicationResponse.Variable.builder()
                                .name(v.getName())
                                .value(v.getValue())
                                .build()).collect(toList()))
                .build();
    }

}
