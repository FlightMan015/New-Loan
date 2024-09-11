package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import de.joonko.loan.user.service.UserPersonalInformationStore;

import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

import io.fusionauth.domain.User;

@Component
public class FusionauthUserMapper {

    @Value("${fusionAuthSyncPersonalDataIgnoreFields}")
    private Set<String> ignoredPersonalDataFields;

    public User merge(User faUser, UserPersonalInformationStore userPersonalInformationStore) {
        new BeanMap(userPersonalInformationStore).forEach((key, val) -> {
            if (ignoredPersonalDataFields != null && ignoredPersonalDataFields.contains(key)) {
                return;
            }
            if ((key != null && val != null)) {
                faUser.data.put(key.toString(), val);
            }
        });
        faUser.firstName = userPersonalInformationStore.getFirstName();
        faUser.lastName = userPersonalInformationStore.getLastName();
        faUser.birthDate = userPersonalInformationStore.getBirthDate();
        faUser.mobilePhone = userPersonalInformationStore.getMobilePhone().replaceFirst("^0*49", "+49");

        return faUser;
    }

}
