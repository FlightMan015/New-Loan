package de.joonko.loan.offer.testdata;

import de.joonko.loan.avro.dto.dac.CustomDACData;
import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.dac.PersonalDetails;

public class OfferRequestManagerITTestData {
    public static DacAccountSnapshot getDacAccountSnapshot(String userUuid, boolean hasSalary) {
        return DacAccountSnapshot.newBuilder()
                .setUserUUID(userUuid)
                .setPersonalDetails(PersonalDetails.newBuilder().build())
                .setCustomDACData(CustomDACData.newBuilder().setHasSalary(hasSalary).build())
                .build();
    }
}
