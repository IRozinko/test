package fintech.lending.core.db;

import fintech.lending.core.application.db.QLoanApplicationEntity;
import fintech.lending.core.creditlimit.db.QCreditLimitEntity;
import fintech.lending.core.discount.db.QDiscountEntity;
import fintech.lending.core.invoice.db.QInvoiceEntity;
import fintech.lending.core.loan.db.QContractEntity;
import fintech.lending.core.loan.db.QInstallmentEntity;
import fintech.lending.core.loan.db.QLoanEntity;
import fintech.lending.core.periods.db.QPeriodEntity;
import fintech.lending.core.product.db.QProductEntity;
import fintech.lending.core.promocode.db.QPromoCodeClientEntity;
import fintech.lending.core.promocode.db.QPromoCodeEntity;
import fintech.lending.core.promocode.db.QPromoCodeSourceEntity;
import fintech.lending.core.snapshot.db.QLoanDailySnapshotEntity;

public class Entities {

    public static final String SCHEMA = "lending";

    public static final QLoanEntity loan = QLoanEntity.loanEntity;
    public static final QProductEntity product = QProductEntity.productEntity;
    public static final QLoanApplicationEntity loanApplication = QLoanApplicationEntity.loanApplicationEntity;
    public static final QCreditLimitEntity creditLimit = QCreditLimitEntity.creditLimitEntity;
    public static final QInvoiceEntity invoice = QInvoiceEntity.invoiceEntity;
    public static final QPeriodEntity period = QPeriodEntity.periodEntity;
    public static final QLoanDailySnapshotEntity loanDailySnapshot = QLoanDailySnapshotEntity.loanDailySnapshotEntity;
    public static final QInstallmentEntity installment = QInstallmentEntity.installmentEntity;
    public static final QContractEntity contract = QContractEntity.contractEntity;
    public static final QDiscountEntity discount = QDiscountEntity.discountEntity;
    public static final QPromoCodeEntity promoCode = QPromoCodeEntity.promoCodeEntity;
    public static final QPromoCodeClientEntity promoCodeClient = QPromoCodeClientEntity.promoCodeClientEntity;
    public static final QPromoCodeSourceEntity promoCodeSource = QPromoCodeSourceEntity.promoCodeSourceEntity;
}
