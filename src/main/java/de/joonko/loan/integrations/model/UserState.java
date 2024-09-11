package de.joonko.loan.integrations.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;


@Data
@Builder
@ToString
public class UserState {
    DacDataState dacDataState;
    PersonalDataState personalDataState;
    OffersState offersState;

}
