package fintech.bo.components.loan;


import com.google.common.collect.Lists;

import java.util.List;

public class LoanConstants {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";

    public static final String STATUS_DETAIL_DISBURSING = "DISBURSING";
    public static final String STATUS_DETAIL_ACTIVE = "ACTIVE";
    public static final String STATUS_DETAIL_RESCHEDULED = "RESCHEDULED";
    public static final String STATUS_DETAIL_LEGAL = "LEGAL";
    public static final String STATUS_DETAIL_DEFAULT = "DEFAULT";
    public static final String STATUS_DETAIL_SOLD = "SOLD";
    public static final String STATUS_DETAIL_REPURCHASED = "REPURCHASED";
    public static final String STATUS_DETAIL_DISBURSING_UPSELL = "DISBURSING_UPSELL";
    public static final String STATUS_DETAIL_DISBURSING_EXTERNALIZED = "EXTERNALIZED";
    public static final String STATUS_DETAIL_PAID = "PAID";
    public static final String STATUS_DETAIL_RENOUNCED_PAID = "RENOUNCED_PAID";
    public static final String STATUS_DETAIL_VOIDED = "VOIDED";
    public static final String STATUS_DETAIL_BROKEN = "BROKEN";
    public static final String STATUS_DETAIL_BROKEN_PAID = "BROKEN_PAID";
    public static final String STATUS_DETAIL_RESCHEDULED_PAID = "RESCHEDULED_PAID";
    public static final String STATUS_DETAIL_LEGAL_PAID = "LEGAL_PAID";
    public static final String STATUS_DETAIL_RENOUNCED = "RENOUNCED";
    public static final String STATUS_DETAIL_ISSUED = "ISSUED";

    public static List<String> getStatusDetails() {
        return Lists.newArrayList(STATUS_DETAIL_DISBURSING,
            STATUS_DETAIL_ACTIVE,
            STATUS_DETAIL_RESCHEDULED,
            STATUS_DETAIL_LEGAL,
            STATUS_DETAIL_DEFAULT,
            STATUS_DETAIL_SOLD,
            STATUS_DETAIL_REPURCHASED,
            STATUS_DETAIL_DISBURSING_UPSELL,
            STATUS_DETAIL_DISBURSING_EXTERNALIZED,
            STATUS_DETAIL_PAID,
            STATUS_DETAIL_RENOUNCED_PAID,
            STATUS_DETAIL_VOIDED,
            STATUS_DETAIL_BROKEN,
            STATUS_DETAIL_BROKEN_PAID,
            STATUS_DETAIL_RESCHEDULED_PAID,
            STATUS_DETAIL_LEGAL_PAID,
            STATUS_DETAIL_RENOUNCED,
            STATUS_DETAIL_ISSUED);
    }

}
