package de.joonko.loan.user.api;

import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.offer.api.model.UserPersonalDetails;
import de.joonko.loan.user.api.model.ConsentResponse;
import de.joonko.loan.user.api.model.ConsentUpdateRequest;
import de.joonko.loan.user.service.UserAdditionalInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.security.Principal;

import static de.joonko.loan.util.HttpUtil.extractClientIPFromRequest;


@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserAdditionalInformationService userAdditionalInformationService;

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> update(Principal principal, @Valid @RequestBody UserPersonalDetails userPersonalDetails) {
        return userAdditionalInformationService.handleUserInput(principal.getName(), userPersonalDetails).map(ResponseEntity::ok);
    }

    @GetMapping(value = "/consent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConsentResponse> consent(final Principal principal) {
        final var consents = userAdditionalInformationService.getUserConsents(principal.getName());
        return ResponseEntity.ok().body(new ConsentResponse(consents));
    }

    @PutMapping(value = "/consent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConsentResponse> consent(final Principal principal, final @Valid @RequestBody ConsentUpdateRequest consentUpdateRequest, final ServerHttpRequest request) {
        final var clientIp = extractClientIPFromRequest(request);
        final var consents = userAdditionalInformationService.saveConsents(principal.getName(), consentUpdateRequest.getConsents(), clientIp);
        return ResponseEntity.ok().body(new ConsentResponse(consents));
    }
}
