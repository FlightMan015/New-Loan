package de.joonko.loan.partner.aion;

import de.joonko.loan.partner.aion.model.CreditApplicationRequest;
import de.joonko.loan.partner.aion.model.CreditApplicationTransmissionData;
import de.joonko.loan.partner.aion.model.PersonalDataTransmissionData;
import de.joonko.loan.partner.aion.model.TransmissionDataType;

import java.util.List;

public class CreditApplicationRequestTestData {

    public static CreditApplicationRequest buildCreditApplicationRequest() {
        return CreditApplicationRequest.builder()
                .variables(List.of(
                        CreditApplicationRequest.Variable.builder()
                                .transmissionDataType(TransmissionDataType.CREDIT_APPLICATION)
                                .transmissionData(CreditApplicationTransmissionData.builder().build())
                                .build(),

                        CreditApplicationRequest.Variable.builder()
                                .transmissionDataType(TransmissionDataType.PERSONAL_DATA)
                                .transmissionData(PersonalDataTransmissionData.builder().build())
                                .build(),

                        CreditApplicationRequest.Variable.builder()
                                .transmissionDataType(TransmissionDataType.PSD2_RAW)
                                .build()
                ))
                .build();
    }
}
