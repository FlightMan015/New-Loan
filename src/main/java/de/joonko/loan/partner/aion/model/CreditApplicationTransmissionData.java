package de.joonko.loan.partner.aion.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CreditApplicationTransmissionData implements TransmissionData {

    @JsonProperty("civil_status")
    private MaritalStatus civilStatus;

    @JsonProperty("num_dependants")
    private Integer numberOfDependants;

    @JsonProperty("cost_of_life_declared")
    private BigDecimal declaredCostOfLife;

    @JsonProperty("flat_status")
    private String flatStatus;

    @JsonProperty("psd2_ext_income")
    private BigDecimal estimatedIncome;

    @JsonProperty("declared_salary")
    private BigDecimal declaredSalary;

    @JsonProperty("psd2_ext_net_income")
    private BigDecimal estimatedNetIncome;

    @JsonProperty("psd2_ext_pension_benefits")
    private BigDecimal estimatedPensionBenefits;

    @JsonProperty("psd2_ext_child_benefits")
    private BigDecimal estimatedChildBenefits;

    @JsonProperty("psd2_ext_other_revenue")
    private BigDecimal estimatedOtherRevenue;

    @JsonProperty("psd2_rental_income")
    private BigDecimal estimatedRentalIncome;

    @JsonProperty("alimony_income")
    private BigDecimal estimatedAlimonyIncome;

    @JsonProperty("disposable_income")
    private BigDecimal estimatedDisposableIncome;

    @JsonProperty("rent_expenditure")
    private BigDecimal estimatedRent;

    @JsonProperty("psd2_alimony_payments")
    private BigDecimal estimatedAlimonyPayments;

    @JsonProperty("psd2_acknowledged_net_income")
    private BigDecimal estimatedAcknowledgedNetIncome;

    @JsonProperty("time_of_employment")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate employmentDate;

    @JsonProperty("request_origin_ip")
    private String requestOriginIp;

    @JsonProperty("request_origin_countrycode")
    private String requestOriginCountry;

    @JsonProperty("name_of_employer")
    private String employerName;

    @JsonProperty("company_nip")
    @Builder.Default
    private String companyNip = "null";

    @JsonProperty("installments_credit_obligations")
    private BigDecimal declaredInstallmentsAndCreditObligations;

    @JsonProperty("installments_credit_obligations_psd2")
    private BigDecimal estimatedInstallmentsAndCreditObligations;

    @JsonProperty("declared_limits")
    private BigDecimal declaredCreditCardsLimit;

    @JsonProperty("declared_purpose_of_loan")
    private String purposeOfLoan;

    @JsonProperty("bonimia_score")
    private String bonimaScore;

    @JsonProperty("place_of_birth")
    private String placeOfBirth;

    @JsonProperty("birth_country")
    private String countryOfBirth;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDate;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("residential_address")
    private ResidentialAddress residentialAddress;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("occupation")
    private Occupation occupation;

    @JsonProperty("source_of_funds")
    private SourceOfFunds sourceOfFunds;

    @JsonProperty("taxability_us")
    @Builder.Default
    private Boolean taxabilityInUS = false;

    @JsonProperty("source_of_wealth")
    @Builder.Default
    private String sourceOfWealth = null;

    @JsonProperty("taxability_de_only")
    @Builder.Default
    private String taxabilityOnlyGermany = "DE";

    @JsonProperty("tin")
    @Builder.Default
    private String tin = null;

    @JsonProperty("requested_amount")
    private Integer requestedAmount;

    @JsonProperty("offer_currency")
    @Builder.Default
    private String offerCurrency = "EUR";

    @JsonProperty("schufa_class")
    private String schufaClass;

    @JsonProperty("is_current_delay_in_installments")
    @Builder.Default
    private Boolean hasDelayInInstallments = false;

    @JsonProperty("is_there_current_delay_in_payment_over_0_days_declared")
    private Boolean hasDeclaredCurrentDelaysInInstallments;

    @JsonProperty("was_there_any_delay_in_installments_in_last_12M_gt_40d_diff")
    @Builder.Default
    private Boolean hadDelayInInstallmentsInLast12M40DaysDiff = false;

    @JsonProperty("was_there_any_delay_in_installments_in_last_12M_gt_62d_diff")
    @Builder.Default
    private Boolean hadDelayInInstallmentsInLast12M62DaysDiff = false;

    @JsonProperty("probability_of_default")
    @Builder.Default
    private BigDecimal probabilityOfDefault = null;

    @JsonProperty("ext_psd2_avg_income_3M")
    private BigDecimal estimatedAverageIncomeInLast3M;

    @JsonProperty("ext_psd2_has_salary_each_month_last3M")
    private Boolean has3MSalary;

    @JsonProperty("ext_psd2_sum_salary_1MAgo")
    private BigDecimal lastSalary;

    @JsonProperty("ext_psd2_sum_salary_2MAgo")
    private BigDecimal salary2MAgo;

    @JsonProperty("ext_psd2_sum_salary_3MAgo")
    private BigDecimal salary3MAgo;

    @Data
    @Builder
    public static class ResidentialAddress {

        @JsonProperty("country")
        private String country;

        @JsonProperty("postal_code")
        private String postalCode;

        @JsonProperty("city")
        private String city;

        @JsonProperty("street")
        private String street;

        @JsonProperty("building_number")
        private String buildingNumber;
    }
}
