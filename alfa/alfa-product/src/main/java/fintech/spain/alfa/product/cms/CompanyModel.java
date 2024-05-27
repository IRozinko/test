package fintech.spain.alfa.product.cms;

import fintech.spain.alfa.product.AlfaConstants;
import lombok.Data;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
public class CompanyModel {

    private String name;
    private String number;
    private String addressLine1;
    private String addressLine2;
    private String phone;
    private String email;
    private String webSite;
    private String incomingSmsNumber;

    private String defaultLocale = AlfaConstants.LOCALE;
    private String currency = AlfaConstants.CURRENCY;

    private String webBaseUrl;
    private String apiBaseUrl;

    private List<BankAccountModel> bankAccounts = newArrayList();
}
