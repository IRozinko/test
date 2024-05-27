package fintech.bo.components.cms;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class CmsConstants {

    public static final String TYPE_NOTIFICATION = "NOTIFICATION";
    public static final String TYPE_PDF_HTML = "PDF_HTML";
    public static final String TYPE_EMBEDDABLE = "EMBEDDABLE";
    public static final String TYPE_TRANSLATION = "TRANSLATION";

    public static final List<String> ALL_TYPES = ImmutableList.of(TYPE_NOTIFICATION, TYPE_PDF_HTML, TYPE_EMBEDDABLE, TYPE_TRANSLATION);
}
