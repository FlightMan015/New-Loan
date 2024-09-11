package de.joonko.loan.partner.santander.mapper;


import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class SantanderGetOfferCurrentEmploymentDetailRequestMapper {

    @Mapping(target = "berufsbezeichnung", ignore = true)
    @Mapping(target = "berufsgruppe", source = ".", qualifiedByName = "toEmploymentType")
    @Mapping(target = "arbeitgeberName", source = "employerName")

    @Mapping(target = "arbeitgeberStrasse", source = "streetName")
    @Mapping(target = "arbeitgeberPlz", source = "zipCode.code")
    @Mapping(target = "arbeitgeberOrt", source = "city")
    @Mapping(target = "arbeitgeberHausnr", source = "houseNumber")


    @Mapping(target = "beschaeftigtSeit", source = "employmentSince")

    @Mapping(target = "beschaeftigtBis", source = "professionEndDate")
    @Mapping(target = "befristetBis", source = "professionEndDate")
    abstract ScbCapsBcoWSStub.BeschaeftigungsverhaeltnisXO toAktuellesBV(EmploymentDetails employmentDetails);

    @Named("toEmploymentType")
    ScbCapsBcoWSStub.BerufType toEmploymentType(EmploymentDetails employmentDetails) {
        switch (employmentDetails.getEmploymentType()) {
            case REGULAR_EMPLOYED:
                return ScbCapsBcoWSStub.BerufType.ANGESTELLTER;
            case OTHER:
                return ScbCapsBcoWSStub.BerufType.UNBEKANNT;
            default:
                throw new RuntimeException(employmentDetails.getEmploymentType() + " employment type not supported");
        }
    }
}
