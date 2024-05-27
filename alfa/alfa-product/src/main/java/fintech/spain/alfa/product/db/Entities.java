package fintech.spain.alfa.product.db;

import fintech.spain.alfa.product.db.*;
import fintech.spain.alfa.product.extension.discounts.db.QExtensionDiscountEntity;
import fintech.spain.alfa.product.loc.db.QLocBatchEntity;

public class Entities {

    public static final String SCHEMA = "alfa";

    public static final fintech.spain.alfa.product.db.QAddressEntity address = fintech.spain.alfa.product.db.QAddressEntity.addressEntity;
    public static final fintech.spain.alfa.product.db.QWealthinessCategoryEntity category = fintech.spain.alfa.product.db.QWealthinessCategoryEntity.wealthinessCategoryEntity;
    public static final QLocBatchEntity locBatchEntity = QLocBatchEntity.locBatchEntity;
    public static final fintech.spain.alfa.product.db.QIdentificationDocumentEntity identificationDocument = fintech.spain.alfa.product.db.QIdentificationDocumentEntity.identificationDocumentEntity;

    public static final fintech.spain.alfa.product.db.QAlfaDailyPenaltyStrategyEntity dailyPenaltyStrategy = fintech.spain.alfa.product.db.QAlfaDailyPenaltyStrategyEntity.alfaDailyPenaltyStrategyEntity;
    public static final fintech.spain.alfa.product.db.QAlfaExtensionStrategyEntity extensionStrategy = fintech.spain.alfa.product.db.QAlfaExtensionStrategyEntity.alfaExtensionStrategyEntity;
    public static final fintech.spain.alfa.product.db.QAlfaMonthlyInterestStrategyEntity interestStrategy = fintech.spain.alfa.product.db.QAlfaMonthlyInterestStrategyEntity.alfaMonthlyInterestStrategyEntity;
    public static final fintech.spain.alfa.product.db.QAlfaFeeStrategyEntity feeStrategy = fintech.spain.alfa.product.db.QAlfaFeeStrategyEntity.alfaFeeStrategyEntity;

    public static final fintech.spain.alfa.product.db.QAlfaDpdPenaltyStrategyEntity dpdPenaltyStrategy = fintech.spain.alfa.product.db.QAlfaDpdPenaltyStrategyEntity.alfaDpdPenaltyStrategyEntity;
    public static final QExtensionDiscountEntity extensionDiscount = QExtensionDiscountEntity.extensionDiscountEntity;
    public static final fintech.spain.alfa.product.db.QLoanReschedulingEntity reschedulingLoan = fintech.spain.alfa.product.db.QLoanReschedulingEntity.loanReschedulingEntity;

}
