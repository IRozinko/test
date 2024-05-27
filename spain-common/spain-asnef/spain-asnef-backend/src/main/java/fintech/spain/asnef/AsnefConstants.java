package fintech.spain.asnef;

public abstract class AsnefConstants {

    public static final String FINANCIAL_PRODUCT_CODE = "07";

    public static final String NATURE_OF_PERSON_CODE = "06";

    public static final String ADDRESS_TYPE_ACTUAL = "ACTUAL";

    public static abstract class Rp {

        public static final String HEADER_RECORD_TYPE = "01";

        public static final String RECORD_TYPE = "02";

        public static final String CONTROL_RECORD_TYPE = "03";

        public static final String DEVO_HEADER_RECORD_TYPE = "D1";

        public static final String DEVO_RECORD_TYPE = "D2";

        public static final String DEVO_CONTROL_RECORD_TYPE = "D3";

        public static String getFilenameTxt(String reportingEntity) {
            return "RPNOTI_" + reportingEntity + ".txt";
        }

        public static String getFilenameZip(String reportingEntity) {
            return "RPNOTI_" + reportingEntity + ".zip";
        }
    }

    public static abstract class Fotoaltas {

        public static final String HEADER_RECORD_TYPE = "010200";

        public static final String OUTPUT_RECORD_TYPE = "010210";

        public static final String CONTROL_RECORD_TYPE = "010299";

        public static final String CURRENCY_TYPE_EURO = "02";

        public static final String OPERATION_SITUATION_CODE_OTHER = "99";

        public static final String NOTIFY_INDICATOR = "S";

        public static final String COUNTRY_CODE_SPAIN = "724";

        public static final String NAME_FORMAT = "2";

        public static final String ADDRESS_FORMAT = "2";

        public static final String PHONE_FORMAT = "2";

        public static final String PHONE_COUNTRY_CODE_SPAIN = "34";

        public static String getFilenameTxt(String reportingEntity) {
            return "E" + reportingEntity + "_fotoaltas.txt";
        }

        public static String getFilenameZip(String reportingEntity) {
            return "E" + reportingEntity + "_fotoaltas.zip";
        }

        public static String getFileIdentifier(String reportingEntity) {
            return reportingEntity + "0001";
        }
    }
}
