package fintech.crm.contacts;

import org.apache.commons.lang3.StringUtils;

public abstract class PhoneNumberUtils {

    public static String normalize(String phone) {
        phone = StringUtils.remove(phone, " ");
        phone = StringUtils.remove(phone, "_");
        phone = StringUtils.removeStart(phone, "+34");
        return phone;
    }
}
