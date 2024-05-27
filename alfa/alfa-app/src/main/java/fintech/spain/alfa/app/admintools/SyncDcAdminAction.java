package fintech.spain.alfa.app.admintools;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.admintools.AdminAction;
import fintech.admintools.AdminActionContext;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.spain.alfa.product.dc.DcFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SyncDcAdminAction implements AdminAction {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private DcFacade dcFacade;

    @Override
    public String getName() {
        return "SyncDebtCollection";
    }

    @Override
    public void execute(AdminActionContext context) {
        List<Long> loanIds = queryFactory.select(Entities.loan.id)
            .from(Entities.loan)
            .where(Entities.loan.maturityDate.isNotNull().and(Entities.loan.statusDetail.ne(LoanStatusDetail.VOIDED)))
            .orderBy(Entities.loan.id.asc()).fetch();
        context.updateProgress("Found " + loanIds.size() + " loans");
        for (int i = 0; i < loanIds.size(); i++) {
            dcFacade.postLoan(loanIds.get(i), null, null, false);
            if (i % 10 == 0) {
                context.updateProgress("Posted " + i + " out of " + loanIds.size() + " loans");
            }
        }
        context.updateProgress("Completed, total " + loanIds.size() + " loans posted");
    }
}
