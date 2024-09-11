package de.joonko.loan.identification.mapper.idnow;

import de.joonko.loan.config.CreditPlusConfig;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.CreditPlusStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class CreditPlusIdentRequestMapper {

    @Autowired
    private CreditPlusConfig creditPlusConfig;

    @Autowired
    private CreditPlusStoreService creditPlusStoreService;

    @Mapping(target = "custom1", source = ".", qualifiedByName = "toCustom1")
    @Mapping(target = "custom2", source = ".", qualifiedByName = "toCustom2")
    @Mapping(target = "custom3", source = ".", qualifiedByName = "toCreditPlusDealerNumber")
    public abstract de.joonko.loan.identification.model.idnow.CreateIdentRequest toIdNowCreateIdentRequest(CreateIdentRequest identificationRequest);

    @Named("toCustom1")
    String toCustom1(CreateIdentRequest identificationRequest) {
        String[] values = new String[4];
        values[0] = creditPlusConfig.getCustom3();
        values[1] = String.valueOf(creditPlusStoreService.getCpTransactionNumber(identificationRequest.getApplicationId(), identificationRequest.getDuration()));
        values[2] = CreditPlusDefaults.FRONTEND_TYPE;
        values[3] = CreditPlusDefaults.DEBTOR_TYPE;

        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        pooledPBEStringEncryptor.setAlgorithm(CreditPlusDefaults.ENCRYPTION_ALGORITHM);
        pooledPBEStringEncryptor.setPassword(CreditPlusDefaults.ENCRYPTIOn_PASSWORD);
        pooledPBEStringEncryptor.setPoolSize(CreditPlusDefaults.POOL_SIZE);

        String encrypt = pooledPBEStringEncryptor.encrypt(StringUtils.join(values, CreditPlusDefaults.DELIMITER));

        return encrypt;
    }

    @Named("toCustom2")
    String toCustom2(CreateIdentRequest identificationRequest) {
        return String.valueOf(creditPlusStoreService.getCpTransactionNumber(identificationRequest.getApplicationId(), identificationRequest.getDuration()));
    }

    @Named("toCreditPlusDealerNumber")
    String toCreditPlusDealerNumber(CreateIdentRequest identificationRequest) {
        return creditPlusConfig.getCustom3();
    }
}
