package fintech.bo.components;

import fintech.bo.api.model.product.ProductType;

public class ProductResolver {

    public static ProductType PRODUCT_TYPE;

    public static void init(ProductType type) {
        PRODUCT_TYPE = type;
    }

    public static boolean isPayday() {
        return PRODUCT_TYPE == ProductType.PAYDAY;
    }

    public static boolean isRevolving() {
        return PRODUCT_TYPE == ProductType.REVOLVING;
    }

    public static boolean isLineOfCredit() {
        return PRODUCT_TYPE == ProductType.LINE_OF_CREDIT;
    }

}
