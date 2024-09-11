package de.joonko.loan.dac.fts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FTSWizardSessionDetailsService {
    private final FTSWizardSessionDetailsRepository ftsWizardSessionDetailsRepository;

    public FTSWizardSessionDetailsStore saveWizardSessionDetails(FTSWizardSessionDetailsStore ftsWizardSessionDetailsStore) {
        return ftsWizardSessionDetailsRepository.save(ftsWizardSessionDetailsStore);
    }
}
