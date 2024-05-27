package fintech.dowjones;

public abstract class DowJonesConstants {

    public static final String RECORD_TYPE = "P";
    public static final String SEARCH_TYPE = "precise";
    public static final String EXCLUDE_DECEASED = "true";
    public static final String HITS_FROM = "0";
    public static final String HITS_TO = "10";

    public static abstract class Keys {
        public static final String NAME = "name";
        public static final String DATE = "date-of-birth";
        public static final String RECORD_TYPE = "record-type";
        public static final String SEARCH_TYPE = "search-type";
        public static final String EXCLUDE_DECEASED = "exclude-deceased";
        public static final String HITS_FROM = "hits-from";
        public static final String HITS_TO = "hits-to";
    }
}
