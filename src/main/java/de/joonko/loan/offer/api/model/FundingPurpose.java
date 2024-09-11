package de.joonko.loan.offer.api.model;

import java.util.Optional;

public enum FundingPurpose {
    NEW_CAR("new_car"),
    OLD_CAR_LESS_3_YEARS("old_car_less_3_years"),
    OLD_CAR_OVER_3_YEARS("old_car_over_3_years"),
    FURNITURE("furniture"),
    RENOVATION("renovation"),
    VACATION("vacation"),
    PC_TV("pc_tv"),
    LOAN_REPAYMENT("loan_repayment"),
    BALANCING_CURRENT_ACCOUNT("balancing_current_account"),
    HOUSE_SHIFT("house_shift"),
    CONSUMER_GOODS("consumer_goods"),
    REAL_ESTATE("real_estate"),
    OTHER("other");

    private String value;

    public String getValue() {
        return this.value;
    }

    FundingPurpose(final String val) {
        this.value = val;
    }

    public static Optional<FundingPurpose> fromValue(final String value) {
        for (FundingPurpose b : FundingPurpose.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return Optional.of(b);
            }
        }
        return Optional.empty();
    }
}
