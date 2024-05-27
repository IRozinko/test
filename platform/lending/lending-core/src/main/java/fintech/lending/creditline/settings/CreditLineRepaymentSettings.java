package fintech.lending.creditline.settings;

import fintech.lending.core.invoice.db.InvoiceItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amount;

@Data
public class CreditLineRepaymentSettings {

    private List<InvoiceItemTypeSubTypePair> invoiceDistributionOrder = newArrayList();

    private List<DistributionOrderType> brokenLoanDistributionOrder = newArrayList();

    private BigDecimal outstandingWriteOffAmount = amount(0);

    @AllArgsConstructor
    @Getter
    @ToString
    public static class InvoiceItemTypeSubTypePair {
        private InvoiceItemType type;
        private String subType;

        public static InvoiceItemTypeSubTypePair typeSubType(InvoiceItemType type, String subType) {
            return new InvoiceItemTypeSubTypePair(type, subType);
        }

        public static InvoiceItemTypeSubTypePair type(InvoiceItemType type) {
            return new InvoiceItemTypeSubTypePair(type, null);
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class DistributionOrderType {
        private String type;
        private String subType;

        public static DistributionOrderType orderTypeSubType(String type, String subType) {
            return new DistributionOrderType(type, subType);
        }

        public static DistributionOrderType orderType(String type) {
            return new DistributionOrderType(type, null);
        }

        public boolean isOfType(String type) {
            return StringUtils.equalsIgnoreCase(this.type, type);
        }
    }

}
