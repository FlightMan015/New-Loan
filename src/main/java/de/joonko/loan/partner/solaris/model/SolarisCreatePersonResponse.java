package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolarisCreatePersonResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("salutation")
    private String salutation;

    @JsonProperty("title")
    private String title;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("contact_address")
    private Address contactAddress;

    private String email;

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("birth_name")
    private String birthName;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @JsonProperty("birth_city")
    private String birthCity;

    @JsonProperty("birth_country")
    private String birthCountry;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("employment_status")
    private EmploymentStatus employmentStatus;

    @JsonProperty("job_title")
    private String jobTitle;

    @JsonProperty("fatca_relevant")
    private Boolean fatcaRelevant;

    @JsonProperty("fatca_crs_confirmed_at")
    private Instant fatcaCrsConfirmedAt;

    @JsonProperty("business_purpose")
    private String businessPurpose;

    @JsonProperty("terms_conditions_signed_at")
    private Instant termsConditionsSignedAt;

    @JsonProperty("tax_information")
    private TaxInformation taxInformation;

    @JsonProperty("industry")
    private String industry;

    @JsonProperty("industry_key")
    private String industryKey;

    @JsonProperty("own_economic_interest_signed_at")
    private Instant ownEconomicInterestSignedAt;

    @JsonProperty("flagged_by_compliance")
    private Boolean flaggedByCompliance;

    @JsonProperty("expected_monthly_revenue_cents")
    private Integer expectedMonthlyRevenueCents;

    @JsonProperty("vat_number")
    private String vatNumber;

    @JsonProperty("website_social_media")
    private String websiteSocialMedia;

    @JsonProperty("business_trading_name")
    private String businessTradingName;

    @JsonProperty("nace_code")
    private String naceCode;

    @JsonProperty("business_address_line_1")
    private String businessAddressLine1;

    @JsonProperty("business_address_line_2")
    private String businessAddressLine2;

    @JsonProperty("business_postal_code")
    private String businessPostalCode;

    @JsonProperty("business_city")
    private String businessCity;

    @JsonProperty("business_country")
    private String businessCountry;

    @JsonProperty("screening_progress")
    private String screeningProgress;

}
