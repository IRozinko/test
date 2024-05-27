package fintech.nordigen;

import fintech.IbanUtils;
import fintech.instantor.model.InstantorAccount;
import fintech.instantor.model.InstantorResponse;
import fintech.instantor.model.InstantorTransaction;
import fintech.nordigen.model.NordigenAccount;
import fintech.nordigen.model.NordigenAccountTransaction;
import fintech.nordigen.model.NordigenRequestBody;

import java.util.stream.Collectors;

public class NordigenUtils {

    public static NordigenRequestBody createNordigenRequestBody(InstantorResponse instantorResponse, String primaryBankAccount) {
        NordigenRequestBody requestBody = new NordigenRequestBody();
        requestBody.setAccountList(
            instantorResponse.getAccounts().stream().filter(a -> IbanUtils.equals(a.getIban(), primaryBankAccount))
                .map(a -> mapToNordigenAccount(instantorResponse, a)).collect(Collectors.toList())
        );
        return requestBody;
    }

    //TODO CHECK with latest Nordigen API
    private static NordigenAccount mapToNordigenAccount(InstantorResponse instantorResponse, InstantorAccount instantorAccount) {
        NordigenAccount nordigenAccount = new NordigenAccount();
        nordigenAccount.setAccountNumber(instantorAccount.getIban());
        nordigenAccount.setHolderName(instantorAccount.getHolderName());
        nordigenAccount.setBankName(instantorResponse.getBankName());
        nordigenAccount.setCurrency(instantorAccount.getCurrency());
        nordigenAccount.setStartBalance(instantorAccount.getBalance());
        nordigenAccount.setEndBalance(instantorAccount.getBalance());
        nordigenAccount.setTransactions(instantorAccount.getTransactionList().stream().map(NordigenUtils::mapToNordigenTransaction).collect(Collectors.toList()));
        return nordigenAccount;
    }

    private static NordigenAccountTransaction mapToNordigenTransaction(InstantorTransaction instantorTransaction) {
        NordigenAccountTransaction transaction = new NordigenAccountTransaction();
        transaction.setDate(instantorTransaction.getDate());
        transaction.setInfo(instantorTransaction.getDescription());
        transaction.setSum(instantorTransaction.getAmount());
        return transaction;

    }

}
