package de.joonko.loan.reporting.api;

import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.reporting.api.model.GetOfferStatusResponse;
import de.joonko.loan.reporting.api.model.GetOffersStatusResponse;
import de.joonko.loan.reporting.api.model.ResponseFormat;
import de.joonko.loan.reporting.api.validator.DateValidator;
import de.joonko.loan.reporting.domain.OfferStatusReportingService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;

import static de.joonko.loan.util.CsvHelperUtil.constructCsv;
import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
@GenericExceptionHandler
@RestController
@RequestMapping("/loan/distribution-channel")
public class OfferStatusController {

    private final OfferStatusReportingService offerStatusReportingService;
    private final DateValidator dateValidator;
    private final OfferStatusMapper offerStatusMapper;

    @GetMapping(value = "/{tenant-id}/status", produces = {"application/json", "text/csv"})
    public Mono<ResponseEntity> getOffersStatus(@PathVariable(name = "tenant-id") UUID tenantId,
                                                @RequestParam(name = "start-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam(name = "end-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                @RequestParam(name = "response-format", required = false, defaultValue = "json") @Validated ResponseFormat responseFormat) {

        log.info("Get offers report for tenantId: {}, startDate: {}, endDate: {}, responseFormat: {}", tenantId, startDate, endDate, responseFormat);

        final var startDateTime = OffsetDateTime.of(startDate, LocalTime.MIN, ZoneOffset.UTC);
        final var endDateTime = OffsetDateTime.of(endDate, LocalTime.MAX, ZoneOffset.UTC);


        return Mono.just(responseFormat)
                .filter(any -> dateValidator.test(startDateTime, endDateTime))
                .flatMap(ignore -> offerStatusReportingService.get(tenantId, startDateTime, endDateTime))
                .map(offersStatus -> GetOffersStatusResponse.builder()
                        .offers(offersStatus.stream()
                                .map(offerStatusMapper::from)
                                .sorted(Comparator.comparing(GetOfferStatusResponse::getLoanAmountRequestedAt).reversed()).collect(toList()))
                        .build())
                .map(resp -> {
                    if (responseFormat == ResponseFormat.CSV) {
                        final var csvFileName = tenantId.toString()
                                .concat("-")
                                .concat(OffsetDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));

                        final var headers = new HttpHeaders();
                        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
                        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");
                        final var csvFile = constructCsv(resp.getOffers());
                        return new ResponseEntity<>(
                                csvFile,
                                headers,
                                HttpStatus.OK
                        );
                    }
                    return ResponseEntity.ok().body(resp);
                });
    }
}
