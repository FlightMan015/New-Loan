package de.joonko.loan.matcher;

import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.math.BigDecimal;
import java.time.ZoneId;

import static de.joonko.loan.common.utils.PhoneNumberUtil.extractPrefixFromPhoneNumber;
import static de.joonko.loan.offer.domain.FamilyStatus.getStatusesFor2AdultsInHousehold;

public class MatcherBox {

    public static Matcher<ScbCapsBcoWSStub.GetKreditvertragsangebot> isLoanDemandCorrectlyMatched(final LoanDemand loanDemand) {
        return new TypeSafeMatcher<>() {

            @Override
            protected boolean matchesSafely(final ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot) {
                final var mappedRequest = getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag();
                return mappedRequest.getAntragId().equals(loanDemand.getLoanApplicationId()) &&
                        mappedRequest.getFinanzierung().getKreditbetragNetto().compareTo(BigDecimal.valueOf(loanDemand.getLoanAsked())) == 0 &&
                        mappedRequest.getFinanzierung().getVerwendungszweck().equals(ScbCapsBcoWSStub.VwzType.STANDARD) &&
                        mappedRequest.getFinanzierung().getRateneinzugZum().equals(ScbCapsBcoWSStub.RateneinzugType.ERSTER_EINES_MONATS) &&
                        mappedRequest.getFinanzierung().getRsv().equals(ScbCapsBcoWSStub.RsvType.OHNE_RSV) &&
                        mappedRequest.getDarlehnsnehmer().getWerbezustimmung() == loanDemand.getConsents().stream().anyMatch(consent -> consent.getConsentState().equals(ConsentState.ACCEPTED)) &&
                        mappedRequest.getDarlehnsnehmer().getErwachseneImHaushalt().intValue() == (getStatusesFor2AdultsInHousehold().contains(loanDemand.getPersonalDetails().getFamilyStatus()) ? 2 : 1) &&
                        mappedRequest.getDarlehnsnehmer().getKinderImHaushalt().intValue() == loanDemand.getPersonalDetails().getNumberOfChildren() &&
                        mappedRequest.getDarlehnsnehmer().getAnzKindergeldber().intValue() == loanDemand.getPersonalDetails().getNumberOfChildren() &&

                        // Personal
                        mappedRequest.getDarlehnsnehmer().getName().equals(loanDemand.getPersonalDetails().getLastName()) &&
                        mappedRequest.getDarlehnsnehmer().getVorname().equals(loanDemand.getPersonalDetails().getFirstName()) &&
                        mappedRequest.getDarlehnsnehmer().getGeburtsdatum().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(loanDemand.getPersonalDetails().getBirthDate()) &&
                        mappedRequest.getDarlehnsnehmer().getAnrede().equals(loanDemand.getPersonalDetails().getGender().equals(Gender.FEMALE) ? ScbCapsBcoWSStub.AnredeType.FRAU : ScbCapsBcoWSStub.AnredeType.HERR) &&
                        mappedRequest.getDarlehnsnehmer().getTelefon().equals(extractPrefixFromPhoneNumber(loanDemand.getContactData().getMobile())) &&
                        mappedRequest.getDarlehnsnehmer().getMobil().equals(extractPrefixFromPhoneNumber(loanDemand.getContactData().getMobile())) &&
                        mappedRequest.getDarlehnsnehmer().getEmail().equals(loanDemand.getContactData().getEmail().getEmailString()) &&

                        // Address
                        mappedRequest.getDarlehnsnehmer().getAktuelleAdresse().getLand().getLandType().equals("DE") &&
                        mappedRequest.getDarlehnsnehmer().getAktuelleAdresse().getOrt().equals(loanDemand.getContactData().getCity()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuelleAdresse().getPlz().equals(loanDemand.getContactData().getZipCode().getCode()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuelleAdresse().getStrasse().equals(loanDemand.getContactData().getStreetName()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuelleAdresse().getHausnr().equals(loanDemand.getContactData().getStreetNumber()) &&

                        mappedRequest.getDarlehnsnehmer().getVorherigeAdresse().getLand().getLandType().equals("DE") &&
                        mappedRequest.getDarlehnsnehmer().getVorherigeAdresse().getOrt().equals(loanDemand.getContactData().getPreviousAddress().getCity()) &&
                        mappedRequest.getDarlehnsnehmer().getVorherigeAdresse().getPlz().equals(loanDemand.getContactData().getPreviousAddress().getPostCode()) &&
                        mappedRequest.getDarlehnsnehmer().getVorherigeAdresse().getStrasse().equals(loanDemand.getContactData().getPreviousAddress().getStreet()) &&
                        mappedRequest.getDarlehnsnehmer().getVorherigeAdresse().getHausnr().equals(loanDemand.getContactData().getPreviousAddress().getHouseNumber()) &&


                        // IBAN
                        mappedRequest.getDarlehnsnehmer().getBankverbindung().getIban().equals(loanDemand.getDigitalAccountStatements().getIban()) &&
                        mappedRequest.getDarlehnsnehmer().getBankverbindung().getKontoinhaber().equals(ScbCapsBcoWSStub.KontoinhaberType.DN1) &&


                        // Employment
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getBerufsbezeichnung() == null &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getBerufsgruppe().equals(loanDemand.getEmploymentDetails().getEmploymentType().equals(EmploymentType.REGULAR_EMPLOYED) ? ScbCapsBcoWSStub.BerufType.ANGESTELLTER : ScbCapsBcoWSStub.BerufType.UNBEKANNT) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getArbeitgeberName().equals(loanDemand.getEmploymentDetails().getEmployerName()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getArbeitgeberStrasse().equals(loanDemand.getEmploymentDetails().getStreetName()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getArbeitgeberHausnr().equals(loanDemand.getEmploymentDetails().getHouseNumber()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getArbeitgeberOrt().equals(loanDemand.getEmploymentDetails().getCity()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getArbeitgeberPlz().equals(loanDemand.getEmploymentDetails().getZipCode().getCode()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getBeschaeftigtSeit().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(loanDemand.getEmploymentDetails().getEmploymentSince()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getBeschaeftigtBis().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(loanDemand.getEmploymentDetails().getProfessionEndDate()) &&
                        mappedRequest.getDarlehnsnehmer().getAktuellesBV().getBefristetBis().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(loanDemand.getEmploymentDetails().getProfessionEndDate()) &&
                        // Finance
                        loanDemand.getPersonalDetails().getHousingType().equals(HousingType.RENT) ? mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getWarmmiete().compareTo(loanDemand.getPersonalDetails().getFinance().getExpenses().getAcknowledgedRent()) == 0 : mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getWarmmiete() == null &&
                        loanDemand.getPersonalDetails().getHousingType().equals(HousingType.RENT) ? mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getWohnart().equals(ScbCapsBcoWSStub.WohnartType.MIETWOHNUNG) : mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getWohnart() == null &&
                        (loanDemand.getPersonalDetails().getFinance().getExpenses().getMortgages() == null || loanDemand.getPersonalDetails().getFinance().getExpenses().getMortgages().compareTo(BigDecimal.ZERO) == 0) ? mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getHypothek() == null : mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getHypothek().compareTo(loanDemand.getPersonalDetails().getFinance().getExpenses().getMortgages()) == 0 &&
                        mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getNettoEinkommen().compareTo(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome()) == 0 &&
                        mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getKindergeld().compareTo(loanDemand.getPersonalDetails().getFinance().getIncome().getChildBenefits()) == 0 &&
                        mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getRentenbezuege().compareTo(loanDemand.getPersonalDetails().getFinance().getIncome().getPensionBenefits()) == 0 &&
                        mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getSonstigeEinnahmen().compareTo(loanDemand.getPersonalDetails().getFinance().getIncome().getOtherRevenue()) == 0 &&
                        mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getMietEinnahmen().compareTo(loanDemand.getPersonalDetails().getFinance().getIncome().getRentalIncome()) == 0 &&
                        mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getUnterhaltEingang().compareTo(loanDemand.getPersonalDetails().getFinance().getIncome().getAlimonyPayments()) == 0 &&
                        mappedRequest.getDarlehnsnehmer().getEinnahmenAusgaben().getUnterhaltZahlung().compareTo(loanDemand.getPersonalDetails().getFinance().getExpenses().getAlimony()) == 0;

            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("The LoanDemand was not correctly mapped to ScbCapsBcoWSStub.GetKreditvertragsangebot");
            }
        };
    }
}
