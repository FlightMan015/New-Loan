package de.joonko.loan.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.ConnectionPoolSettings;

import de.joonko.loan.converter.FromOptionalConverter;
import de.joonko.loan.converter.OffsetDateTimeToStringConverter;
import de.joonko.loan.converter.StringToOffsetDateTimeConverter;
import de.joonko.loan.converter.ToOptionalConverter;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import lombok.RequiredArgsConstructor;

import static java.util.Arrays.asList;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mongo.ssl")
@RequiredArgsConstructor
public class MongoDbConfig {

    private final MongoSslConfig mongoSSLConfig;

    @Bean
    public MongoClientSettings mongoClientSettings() {
        boolean sslEnabled = mongoSSLConfig.getIsSslEnabled().equals("true");
        if (sslEnabled) {
            return MongoClientSettings.builder()
                    .applyToSslSettings(builder -> {
                        try {
                            builder.context(getSslContext());
                        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException | KeyManagementException e) {
                            log.error("Failed setting ssl context", e);
                            throw new IllegalStateException(e);
                        }
                        builder.enabled(true);
                    })
                    .applyToConnectionPoolSettings((ConnectionPoolSettings.Builder builder) -> {
                        builder.maxSize(100) //connections count
                                .minSize(5)
                                .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                                .maxConnectionIdleTime(5, TimeUnit.MINUTES);
                    })
                    .applyToSocketSettings(builder -> {
                        builder.connectTimeout(30000, TimeUnit.MILLISECONDS);
                    })
                    .build();
        }
        return MongoClientSettings.builder().build();
    }

    private SSLContext getSslContext() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        var jksFile = new File(mongoSSLConfig.getTrustStorePath());
        KeyStore trustStore;
        try (InputStream stream = new FileInputStream(jksFile)) {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(stream, mongoSSLConfig.getTrustStorePassword().toCharArray());
        }
        var factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(trustStore);
        TrustManager[] managers = factory.getTrustManagers();

        var context = SSLContext.getInstance("TLSv1.2");
        context.init(null, managers, null);
        return context;
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(asList(
                new OffsetDateTimeToStringConverter(),
                new StringToOffsetDateTimeConverter(),
                new FromOptionalConverter<>(),
                new ToOptionalConverter()
        ));
    }

    @Autowired
    private MongoDatabaseFactory mongoDbFactory;

    @Bean
    public MongoTemplate mongoTemplate() {
        var converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return new MongoTemplate(mongoDbFactory, converter);
    }
}
