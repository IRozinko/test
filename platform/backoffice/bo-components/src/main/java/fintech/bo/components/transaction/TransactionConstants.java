package fintech.bo.components.transaction;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class TransactionConstants {

    public static final String TRANSACTION_TYPE_DISBURSEMENT = "DISBURSEMENT";
    public static final String TRANSACTION_TYPE_REPAYMENT = "REPAYMENT";
    public static final String TRANSACTION_TYPE_APPLY_PENALTY = "APPLY_PENALTY";
    public static final String TRANSACTION_TYPE_APPLY_INTEREST = "APPLY_INTEREST";
    public static final String TRANSACTION_TYPE_APPLY_FEE = "APPLY_FEE";
    public static final String TRANSACTION_TYPE_FEE_PAYMENT = "FEE_PAYMENT";
    public static final String TRANSACTION_TYPE_PAYMENT = "PAYMENT";
    public static final String TRANSACTION_TYPE_OVERPAYMENT = "OVERPAYMENT";

    public static final String TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS = "UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS";
    public static final String TRANSACTION_SUB_TYPE_FAULTY_OUT = "FAULTY_OUT";
    public static final String TRANSACTION_SUB_TYPE_BANK_COMMISSION = "BANK_COMMISSION";
    public static final String TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER = "INTER_COMPANY_TRANSFER";
    public static final String TRANSACTION_SUB_TYPE_OTHER = "OTHER";
    public static final String TRANSACTION_SUB_TYPE_PRINCIPAL_VIVENTOR = "PRINCIPAL_VIVENTOR";

    public static final List<String> ALL_TRANSACTION_TYPES;

    static {
        List<String> txTypes = new ArrayList<>();
        txTypes.add(TRANSACTION_TYPE_DISBURSEMENT);
        txTypes.add(TRANSACTION_TYPE_REPAYMENT);
        txTypes.add(TRANSACTION_TYPE_APPLY_PENALTY);
        txTypes.add(TRANSACTION_TYPE_APPLY_INTEREST);
        txTypes.add(TRANSACTION_TYPE_APPLY_FEE);
        txTypes.add(TRANSACTION_TYPE_FEE_PAYMENT);
        txTypes.add(TRANSACTION_TYPE_PAYMENT);
        txTypes.add(TRANSACTION_TYPE_OVERPAYMENT);
        txTypes.sort(String::compareTo);
        ALL_TRANSACTION_TYPES = ImmutableList.copyOf(txTypes);
    }
}
