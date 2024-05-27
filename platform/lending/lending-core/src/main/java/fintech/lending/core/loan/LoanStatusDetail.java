package fintech.lending.core.loan;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum LoanStatusDetail {

    DISBURSING,
    ISSUED,
    ACTIVE,
    PAID,
    VOIDED,
    RENOUNCED,
    RENOUNCED_PAID,
    BROKEN,
    BROKEN_PAID,
    RESCHEDULED,
    RESCHEDULED_PAID,
    LEGAL,
    LEGAL_PAID,
    DEFAULT,
    SOLD,
    REPURCHASED,
    DISBURSING_UPSELL,
    EXTERNALIZED;

    public static Set<LoanStatusDetail> paidStates = ImmutableSet.of(
        PAID, BROKEN_PAID, RESCHEDULED_PAID, LEGAL_PAID, RENOUNCED_PAID
    );
}
