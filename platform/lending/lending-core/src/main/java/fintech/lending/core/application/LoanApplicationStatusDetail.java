package fintech.lending.core.application;

import java.util.Arrays;

public class LoanApplicationStatusDetail {

    public static final String PENDING = "PENDING";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String CANCELLED = "CANCELLED";

    public static final String PENDING_INCOMPLETE = "PENDING INCOMPLETE";
    public static final String PENDING_INSTANTOR = "PENDING INSTANTOR";
    public static final String PENDING_MANUAL = "PENDING MANUAL";

    public static boolean isPending(String statusDetail) {
        return Arrays.asList(PENDING, PENDING_INCOMPLETE, PENDING_INSTANTOR, PENDING_MANUAL).contains(statusDetail);
    }

    public static boolean isRejected(String statusDetail) {
        return REJECTED.equals(statusDetail);
    }
}
