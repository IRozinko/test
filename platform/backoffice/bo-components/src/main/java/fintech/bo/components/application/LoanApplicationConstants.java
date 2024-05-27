package fintech.bo.components.application;

import com.google.common.collect.Lists;

import java.util.List;

public class LoanApplicationConstants {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";

    public static final String STATUS_DETAIL_PENDING = "PENDING";
    public static final String STATUS_DETAIL_APPROVED = "APPROVED";
    public static final String STATUS_DETAIL_REJECTED = "REJECTED";
    public static final String STATUS_DETAIL_CANCELLED = "CANCELLED";

    public static final String STATUS_DETAIL_PENDING_INCOMPLETE = "PENDING INCOMPLETE";
    public static final String STATUS_DETAIL_PENDING_INSTANTOR = "PENDING INSTANTOR";
    public static final String STATUS_DETAIL_PENDING_MANUAL = "PENDING MANUAL";


    public static final String SOURCE_TYPE_ORGANIC = "ORGANIC";
    public static final String SOURCE_TYPE_AFFILIATE = "AFFILIATE";

    public static List<String> getLoanApplicationStatusDetails() {
        return Lists.newArrayList(STATUS_DETAIL_PENDING,
            STATUS_DETAIL_APPROVED,
            STATUS_DETAIL_REJECTED,
            STATUS_DETAIL_CANCELLED,
            STATUS_DETAIL_PENDING_INCOMPLETE,
            STATUS_DETAIL_PENDING_INSTANTOR,
            STATUS_DETAIL_PENDING_MANUAL);
    }

}
