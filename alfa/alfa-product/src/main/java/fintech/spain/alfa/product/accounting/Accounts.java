package fintech.spain.alfa.product.accounting;

import fintech.accounting.Account;
import fintech.accounting.AccountingService;
import fintech.accounting.AddAccountCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Accounts {
    public static final String LOANS_PRINCIPAL = "4302.0";
    public static final String LOANS_CHARGED = "4302.1";
    public static final String OVERPAYMENT = "4302.2";
    public static final String UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS = "4302.3";
    public static final String FUNDS_IN_TRANSFER = "5728.0";

    public static final String BANK_CAIXA = "57202.0";
    public static final String BANK_BANKIA = "57204.0";
    public static final String BANK_SABADELL = "57209.0";
    public static final String BANK_BBVA = "572012.0";
    public static final String BANK_ING = "572015.0";
    public static final String INTERCOMPANY_TRANSFER = "5728.1";
    public static final String BANK_PAY_TPV = "5728.2";

    public static final String PRIMARY_BANK_ACCOUNT = BANK_ING;

    public static final String INITIAL_COMMISSION = "705.1";
    public static final String PROLONGS = "705.2";
    public static final String PENALTIES = "705.3";
    public static final String PREPAYMENT_COMMISSION = "705.6";
    public static final String RESCHEDULE_COMMISSION = "705.9";
    public static final String FAULTY_OUT = "562.1";

    public static final String WRITE_OFF_PRINCIPAL = "6942.0";
    public static final String WRITE_OFF_COMMISSIONS_EARLY_PAYMENT = "6942.1";
    public static final String WRITE_OFF_COMMISSIONS_DC_DISCOUNT = "6942.2";
    public static final String WRITE_OFF_SALES_PORTFOLIO = "6942.3";


    public static final String ALL_TYPE_BANK_FEES = "626.4";
    public static final String ROUNDING = "669.2";
    public static final String VIVENTOR_PRINCIPAL = "521.5";
    public static final String OTHER = "9999";

    public static final String UNNAX = "572.0";
    public static final String BJS = "9999.0";




    @Autowired
    private AccountingService accountingService;

    public void init() {
        addAccount(LOANS_PRINCIPAL, "Principal");
        addAccount(LOANS_CHARGED, "Loans charged (commissions)");
        addAccount(OVERPAYMENT, "Overpayment");
        addAccount(UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS, "Unidentified liabilities to customers");
        addAccount(FUNDS_IN_TRANSFER, "Funds in transfer");

        addAccount(BANK_CAIXA, "Caixa bank");
        addAccount(BANK_BANKIA, "Bankia Bank");
        addAccount(BANK_SABADELL, "Sabadell Bank");
        addAccount(BANK_BBVA, "Bbva Bank");
        addAccount(BANK_ING, "Ing Bank");
        addAccount(INTERCOMPANY_TRANSFER, "Intercompany transfer");
        addAccount(BANK_PAY_TPV, "Pay Tpv Bank");

        addAccount(INITIAL_COMMISSION, "Initial commission");
        addAccount(PROLONGS, "Prolongs");
        addAccount(PENALTIES, "Penalties");
        addAccount(PREPAYMENT_COMMISSION, "Prepayment commission");
        addAccount(RESCHEDULE_COMMISSION, "Reschedule commission");
        addAccount(FAULTY_OUT, "Faulty out");

        addAccount(WRITE_OFF_PRINCIPAL, "Writte off principal");
        addAccount(WRITE_OFF_COMMISSIONS_EARLY_PAYMENT, "Write off commissions early payment");
        addAccount(WRITE_OFF_COMMISSIONS_DC_DISCOUNT, "Write off commissions dc discount");
        addAccount(WRITE_OFF_SALES_PORTFOLIO, "Write off sales portfolio");

        addAccount(ALL_TYPE_BANK_FEES, "All type bank fees");
        addAccount(ROUNDING, "Rounding");
        addAccount(VIVENTOR_PRINCIPAL, "Viventor principal");

        addAccount(OTHER, "Other");

    }

    private void addAccount(String code, String name) {
        Optional<Account> account = accountingService.findAccount(code);
        if (!account.isPresent()) {
            accountingService.addAccount(new AddAccountCommand(code, name));
        }
    }

}
