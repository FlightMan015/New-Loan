package de.joonko.loan.identification.config;

import de.joonko.loan.identification.model.idnow.DocumentDefinition;
import de.joonko.loan.identification.model.idnow.IDNowJwtToken;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.idnow.IdNowAccountMapper;
import de.joonko.loan.identification.service.idnow.IdNowClientApi;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
@Slf4j
@Component
@Profile("!integration")
public class IDNowAccountSetupConfig {

    private final IdNowClientApi idNowClientApi;
    private final AccountActiveConfig accountActiveConfig;
    private final IdNowAccountMapper idNowAccountMapper;

    @PostConstruct
    public void syncDocumentDefinitions() {
        Flux.fromIterable(accountActiveConfig.getActiveIdNowAccounts())
                .flatMap(this::uploadMissingDocumentDefinitions)
                .subscribe();
    }

    private Flux<Void> uploadMissingDocumentDefinitions(IdNowAccount account) {
        return Mono.just(account)
                .flatMap(idNowClientApi::getJwtToken)
                .map(IDNowJwtToken::getAuthToken)
                .zipWhen(authToken -> getMissingDefinitions(account, authToken))
                .flatMapMany(tuple -> uploadMissingDocumentDefinitions(account, tuple.getT1(), tuple.getT2()));
    }

    private Flux<Void> uploadMissingDocumentDefinitions(IdNowAccount account, String authToken, Set<DocumentDefinition> missingDefinitions) {
        return Flux.fromIterable(missingDefinitions)
                .flatMap(missingDef -> idNowClientApi.createDocumentDefinition(account, authToken, missingDef));
    }

    private Mono<Set<DocumentDefinition>> getMissingDefinitions(IdNowAccount account, String authToken) {
        return idNowClientApi.getDocumentDefinitions(account, authToken)
                .map(List::of)
                .map(existingDefinitions -> filterMissingDefinitions(existingDefinitions, idNowAccountMapper.getDocumentDefinitions(account)));
    }

    private Set<DocumentDefinition> filterMissingDefinitions(List<DocumentDefinition> existingDefinitions, Set<DocumentDefinition> expectedDefinitions) {
        return expectedDefinitions.stream()
                .filter(expectedDefinition -> existingDefinitions.stream()
                        .map(DocumentDefinition::getIdentifier)
                        .noneMatch(existingIdentifier -> existingIdentifier.equals(expectedDefinition.getIdentifier())))
                .collect(toSet());
    }
}
