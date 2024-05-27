package fintech.bo.components.workflow;

import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.lending.tables.LoanApplication.LOAN_APPLICATION;
import static fintech.bo.db.jooq.workflow.Tables.WORKFLOW_;

@Slf4j
public class WorkflowDataProvider extends JooqClientDataProvider<Record> {

    private Long clientId;
    private Long loanId;
    private Long applicationId;

    public WorkflowDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                WORKFLOW_.fields(),
                CLIENT.CLIENT_NUMBER,
                CLIENT.FIRST_NAME,
                CLIENT.LAST_NAME,
                EMAIL_CONTACT.EMAIL,
                LOAN.LOAN_NUMBER,
                LOAN_APPLICATION.APPLICATION_NUMBER))
            .from(WORKFLOW_
                .leftJoin(CLIENT).on(WORKFLOW_.CLIENT_ID.eq(CLIENT.ID))
                .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY))
                .leftJoin(LOAN).on(WORKFLOW_.LOAN_ID.eq(LOAN.ID))
                .leftJoin(LOAN_APPLICATION).on(WORKFLOW_.APPLICATION_ID.eq(LOAN_APPLICATION.ID)));

        if (clientId != null) {
            select.where(WORKFLOW_.CLIENT_ID.eq(clientId));
        }
        if (loanId != null) {
            select.where(WORKFLOW_.LOAN_ID.eq(loanId));
        }
        if (applicationId != null) {
            select.where(WORKFLOW_.APPLICATION_ID.eq(applicationId));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(WORKFLOW_.ID);
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
