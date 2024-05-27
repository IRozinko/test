package fintech.bo.components.accounting;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import fintech.bo.api.client.AccountingApiClient;
import fintech.bo.api.model.accounting.AccountTrialBalance;
import fintech.bo.api.model.accounting.AccountingReportQuery;
import fintech.retrofit.RetrofitHelper;

import java.util.List;
import java.util.stream.Stream;

public class AccountingTrialBalanceProvider extends AbstractBackEndDataProvider<AccountTrialBalance, AccountingReportQuery> {

    private AccountingApiClient api;

    private AccountingReportQuery query = new AccountingReportQuery();

    private List<AccountTrialBalance> body = Lists.newArrayList();

    public AccountingTrialBalanceProvider(AccountingApiClient api) {
        this.api = api;
    }

    @Override
    protected Stream<AccountTrialBalance> fetchFromBackEnd(Query<AccountTrialBalance, AccountingReportQuery> query) {
        body = RetrofitHelper.syncCall(api.getTrialBalance(this.query)).orElseThrow(IllegalStateException::new);

        return body.stream();
    }

    @Override
    protected int sizeInBackEnd(Query<AccountTrialBalance, AccountingReportQuery> query) {
        return body.size();
    }

    public AccountingReportQuery getQuery() {
        return query;
    }

    public void setQuery(AccountingReportQuery query) {
        this.query = query;
    }

    public void setPaymentId(Long paymentId) {
        this.query.setPaymentId(paymentId);
    }

    public void setLoanId(Long loanId) {
        this.query.setLoanId(loanId);
    }

    public void setClientId(Long clientId) {
        this.query.setClientId(clientId);
    }
}
