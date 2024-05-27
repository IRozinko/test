package fintech.bo.components.payments;

public class PaymentConstants {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";

    public static final String STATUS_PROCESSED = "PROCESSED";
    public static final String STATUS_MANUAL = "MANUAL";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VOIDED = "VOIDED";

    public static final String[] ALL_STATUSES = {STATUS_MANUAL, STATUS_PENDING, STATUS_PROCESSED, STATUS_VOIDED};

    public static final String TYPE_INCOMING = "INCOMING";
    public static final String TYPE_OUTGOING = "OUTGOING";

    public static final String[] ALL_TYPES = {TYPE_INCOMING, TYPE_OUTGOING};
}
