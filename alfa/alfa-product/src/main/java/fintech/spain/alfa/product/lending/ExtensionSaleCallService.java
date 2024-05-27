package fintech.spain.alfa.product.lending;

import fintech.lending.core.loan.Installment;

import java.util.List;

public interface ExtensionSaleCallService {
    List<Installment> findInstallmentsWithoutTask();

    void createTasks(List<Installment> installments);
}
