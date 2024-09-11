package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.common.partner.creditplus.auth.CreditPlusAuthService;
import de.joonko.loan.config.CreditPlusConfig;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;

@RequiredArgsConstructor
@Slf4j
@Component
public class CreditPlusContractGateway {

    private final CreditPlusConfig creditPlusConfig;
    private final CreditPlusAuthService creditPlusAuthService;

    public Mono<byte[]> getContract(String applicationId, Integer duration) {

        String dealerOrderNumber = applicationId.concat(String.valueOf(duration));
        log.info("Sending request for {} on CreditPlus", dealerOrderNumber);

        EfinComparerServiceStub efinComparerServiceStub;
        try {
            efinComparerServiceStub = new EfinComparerServiceStub(creditPlusConfig.getHost().concat(creditPlusConfig.getService()));

            EfinComparerServiceStub.GetDocumentVideoIdentE getDocumentVideoIdentE = new EfinComparerServiceStub.GetDocumentVideoIdentE();
            EfinComparerServiceStub.GetDocumentVideoIdent getDocumentVideoIdent = new EfinComparerServiceStub.GetDocumentVideoIdent();
            getDocumentVideoIdent.setDealerOrderNumber(dealerOrderNumber);
            getDocumentVideoIdentE.setGetDocumentVideoIdent(getDocumentVideoIdent);

            efinComparerServiceStub._getServiceClient().addHeader(creditPlusAuthService.getServiceClient());

            EfinComparerServiceStub.GetDocumentVideoIdentResponseE getContractsResponseE = null;
            getContractsResponseE = efinComparerServiceStub.getDocumentVideoIdent(getDocumentVideoIdentE);

            String aReturn = getContractsResponseE.getGetDocumentVideoIdentResponse().get_return();
            byte[] bytes = Base64.getDecoder().decode(aReturn.getBytes());
            return Mono.just(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
