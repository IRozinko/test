package fintech.bo.components.payments.disbursement;

public class DisbursementConstants {

    public static final String STATUS_DETAIL_PENDING = "PENDING";
    public static final String STATUS_DETAIL_EXPORTED = "EXPORTED";
    public static final String STATUS_DETAIL_SETTLED = "SETTLED";
    public static final String STATUS_DETAIL_INVALID = "INVALID";
    public static final String STATUS_DETAIL_VOIDED = "VOIDED";
    public static final String STATUS_DETAIL_CANCELLED = "CANCELLED";
    public static final String STATUS_DETAIL_ERROR = "ERROR";
    public static final String STATUS_DETAIL_EXPORT_ERROR = "EXPORT_ERROR";

    public static final String[] ALL_STATUS_DETAILS = {
        STATUS_DETAIL_PENDING, STATUS_DETAIL_EXPORTED, STATUS_DETAIL_SETTLED, STATUS_DETAIL_INVALID,
        STATUS_DETAIL_VOIDED, STATUS_DETAIL_CANCELLED, STATUS_DETAIL_ERROR, STATUS_DETAIL_EXPORT_ERROR
    };
}
