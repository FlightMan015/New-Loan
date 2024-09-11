package de.joonko.loan.partner.solaris;

import de.joonko.loan.partner.solaris.model.SolarisSignedDocTrail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolarisSignedDocService {
    private final SolarisSignedDocRepository solarisSignedDocRepository;

    public List<SolarisSignedDocTrail> getApplicationsNotSent() {
        return solarisSignedDocRepository.findByEmailSentIsFalse();
    }

    public void updateEmailSentStatusToTrue(String applicationId) {

        SolarisSignedDocTrail solarisSignedDocTrail = solarisSignedDocRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException(("Solaris signed doc trail not present for aplicationID " + applicationId)));
        solarisSignedDocTrail.setEmailSent(Boolean.TRUE);
        solarisSignedDocRepository.save(solarisSignedDocTrail);
    }

    public void save(SolarisSignedDocTrail solarisSignedDocTrail) {
        solarisSignedDocRepository.findByApplicationId(solarisSignedDocTrail.getApplicationId())
                .ifPresentOrElse((value) -> log.info("Document already present not saving"), () -> solarisSignedDocRepository.save(solarisSignedDocTrail));
    }
}
