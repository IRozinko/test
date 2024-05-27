package fintech.spain.alfa.product.utils;

import com.google.common.base.Joiner;
import fintech.crm.address.ClientAddress;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public class SpainAddressUtils {

    public static String fullAddress(ClientAddress address) {
        return Joiner.on(", ").skipNulls().join(
            defaultIfBlank(address.getStreet(), null),
            defaultIfBlank(address.getHouseNumber(), null),
            defaultIfBlank(address.getProvince(), null),
            defaultIfBlank(address.getCity(), null),
            defaultIfBlank(address.getPostalCode(), null)
        );
    }

    public static String addressLine1(ClientAddress address) {
        return Joiner.on(", ").skipNulls().join(
            defaultIfBlank(address.getStreet(), null),
            defaultIfBlank(address.getHouseNumber(), null)
        );
    }

    public static String addressLine2(ClientAddress address) {
        return Joiner.on(", ").skipNulls().join(
            defaultIfBlank(address.getProvince(), null),
            defaultIfBlank(address.getCity(), null),
            defaultIfBlank(address.getPostalCode(), null)
        );
    }
}
