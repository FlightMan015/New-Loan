package de.joonko.loan.db.service;

import de.joonko.loan.db.repositories.LoanDemandStoreRepository;
import de.joonko.loan.db.vo.ExternalIdentifiers;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.util.EncrDecrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanDemandStoreService {

    private final LoanDemandStoreRepository loanApplicationStoreRepository;

    private final EncrDecrService encrDecrService;

    public LoanDemandStore saveLoanDemand(LoanDemandRequest loanDemandRequest, boolean internalUse, String userUUID) {
        LoanDemandStore loanDemandStore = LoanDemandStore.builder()
                .dacId(loanDemandRequest.getDacId())
                .ftsTransactionId(loanDemandRequest.getFtsTransactionId())
                .firstName(encrDecrService.anonymize(loanDemandRequest.getPersonalDetails().getFirstName()))
                .lastName(encrDecrService.anonymize(loanDemandRequest.getPersonalDetails().getLastName()))
                .emailId(encrDecrService.anonymize(loanDemandRequest.getContactData().getEmail()))
                .internalUse(internalUse)
                .userUUID(userUUID)
                .build();
        LoanDemandStore loanDemandStoreWithLoanApplicationId = loanApplicationStoreRepository.save(loanDemandStore);
        loanDemandStoreWithLoanApplicationId.setExternalIdentifiers(ExternalIdentifiers.fromLoanApplicationId(loanDemandStoreWithLoanApplicationId.getApplicationId()));
        return loanApplicationStoreRepository.save(loanDemandStoreWithLoanApplicationId);
    }

    // Remove the method after migration
    public void gpdrMigration() {
        loanApplicationStoreRepository.findAll().forEach(record -> {
            String email = record.getEmailId();
            String firstName = record.getFirstName();
            if (firstName != null && !firstName.matches(".*\\d.*")) {
                if (!StringUtils.isEmpty(email)) {
                    record.setEmailId(encrDecrService.anonymize(record.getEmailId()));
                }

                record.setFirstName(encrDecrService.anonymize(record.getFirstName()));
                record.setLastName(encrDecrService.anonymize(record.getLastName()));
                loanApplicationStoreRepository.save(record);
            }
        });
    }

    public Flux<LoanDemandStore> findByUserId(final String userId) {
        return Mono.fromCallable(() -> loanApplicationStoreRepository.findByUserUUID(userId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<LoanDemandStore> deleteById(final String id) {
        return Mono.fromCallable(() -> loanApplicationStoreRepository.deleteByApplicationId(id))
                .subscribeOn(Schedulers.elastic());
    }

    public Optional<LoanDemandStore> findById(String id) {
        return loanApplicationStoreRepository.findById(id);
    }

    public String getDacId(String applicationId) {
        Optional<LoanDemandStore> loanDemandStore = findById(applicationId);
        return loanDemandStore.isPresent() ? loanDemandStore.get().getDacId() : "";
    }
}
