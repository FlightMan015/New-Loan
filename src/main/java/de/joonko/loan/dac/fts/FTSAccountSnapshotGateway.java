package de.joonko.loan.dac.fts;

import de.joonko.loan.config.FtsConfig;
import de.joonko.loan.dac.fts.model.FtsRawData;
import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.util.EncrDecrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class FTSAccountSnapshotGateway {

    private final FtsConfig ftsConfig;

    @Qualifier("ftsWebClient")
    private final WebClient ftsWebClient;


    public InputStream getAccountSnapshot(String transactionID, String format) {
        try {
            String downloadSnapshotEndpoint = createDownloadSnapshotEndpoint(transactionID, format);
            if (downloadSnapshotEndpoint.startsWith("https")) {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(downloadSnapshotEndpoint).openConnection();
                conn.setSSLSocketFactory(getSslContext().getSocketFactory());
                conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Basic " + getAuthorization());
                final var transactions = conn.getInputStream();
                log.info("Successfully fetched transactions from FTS from url {}, file size - {}", downloadSnapshotEndpoint, transactions.available());
                return transactions;
            } else { // this is done for test case since it runs on localhost and it's http
                HttpURLConnection conn = (HttpURLConnection) new URL(downloadSnapshotEndpoint).openConnection();
                conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Basic " + getAuthorization());
                return conn.getInputStream();
            }

        } catch (Exception exc) {
            throw new RuntimeException("Failed to fetch the attachment from FTS :" + exc);
        }
    }

    public Mono<FtsRawData> fetchAccountSnapshotJson(final String transactionId) {
        return ftsWebClient
                .get()
                .uri(getFtsUri(transactionId))
                .retrieve()
                .bodyToMono(FtsRawData.class)
                .doOnError(e -> log.error("Failed fetching or deserializing FTS raw data to FtsRawData for transactionId {}, exception happened - {}, with cause - {}", transactionId, e.getMessage(), e.getCause()));
    }

    private String getAuthorization() {
        return Base64.getEncoder().encodeToString(("api" + ":" + ftsConfig.getApiKey()).getBytes());
    }

    private String createDownloadSnapshotEndpoint(String ftsTransactionID, String format) {
        return UriComponentsBuilder.fromHttpUrl(ftsConfig.getEndPointUrl())
                .path(ftsTransactionID)
                .path("/accountSnapshot")
                .queryParam("format", format)
                .build().toUriString();
    }

    private SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {

        //trust all certificates for the connection.
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }
                }};

        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, trustAllCerts, new java.security.SecureRandom());
        return context;
    }

    private Function<UriBuilder, URI> getFtsUri(final String transactionId) {
        return uriBuilder ->
                uriBuilder.path(transactionId)
                        .path("/accountSnapshot")
                        .queryParam("format", DomainDefault.FTS_QUERY_PARAM_VALUE_JSON)
                        .build();
    }

}
