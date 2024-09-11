package de.joonko.loan.common.domain;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;

@Getter
public enum Bank {
    AUXMONEY("AUXMONEY"),
    CONSORS("Consors Finanz"),
    DEUTSCHE_FINANZ_SOZIETÄT("Deutsche Finanz Sozietät Privatkredit"),
    MOUNTAIN_BANK("MOUNTAIN_BANK"),
    SWK_BANK("SWK_BANK"),
    SANTANDER("SANTANDER"),
    CREDIT_PLUS("Creditplus Bank"),
    AION("AION"),
    POSTBANK("POSTBANK");

    public final String label;

    public static Bank fromLabel(String label) {
        for (Bank b : Bank.values()) {
            if (b.getLabel()
                    .equalsIgnoreCase(label)) {
                return b;
            }
        }
        return null;
    }

    public static Optional<Bank> fromValue(final String bank) {
        for (Bank b : Bank.values()) {
            if (b.name().equals(bank)) {
                return Optional.of(b);
            }
        }
        return Optional.empty();
    }

    Bank(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static Set<Bank> getExternalBanks() {
        return EnumSet.of(CONSORS, SWK_BANK, SANTANDER, POSTBANK);
    }

    public static Bank getBonifyBank() {
        return Bank.AION;
    }

    public static Set<Bank> getAllBanks() {
        return EnumSet.of(CONSORS, SWK_BANK, SANTANDER, AION, POSTBANK);
    }

    public static Set<Bank> getStatusReportBanks() {
        return EnumSet.of(CONSORS, SWK_BANK);
    }
}
