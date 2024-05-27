package fintech.dc.spi.handlers;

import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Component
public class PromiseToPayBulkAction implements BulkActionHandler {

    @Autowired
    private DebtRepository debtRepository;

    @Override
    public void handle(BulkActionContext context) {
        DebtEntity debt = debtRepository.getRequired(context.getDebt().getId());
        String dueDateText = context.getRequiredParam("dueDate", String.class);
        LocalDate dueDate = LocalDate.parse(dueDateText);
        BigDecimal amount = amount(context.getParam("amount", Number.class).orElse(0.0d).doubleValue());
        debt.setPromiseDueDate(dueDate);
        debt.setPromiseAmount(amount);
    }
}
