package fintech.spain.alfa.web.models.convertor;

import com.google.common.base.Converter;
import fintech.lending.core.loan.Installment;
import fintech.spain.alfa.web.models.InstallmentInfo;

import static fintech.BigDecimalUtils.amount;

public class InstallmentInfoConverter extends Converter<Installment, InstallmentInfo> {

    public static final InstallmentInfoConverter INSTANCE = new InstallmentInfoConverter();

    @Override
    protected InstallmentInfo doForward(Installment installment) {
        return new InstallmentInfo()
            .setInstallmentNumber(installment.getInstallmentNumber())
            .setStatusDetail(installment.getStatusDetail().name())
            .setDueDate(installment.getDueDate())
            .setTotalDue(amount(installment.getTotalDue()))
            .setTotalPaid(amount(installment.getTotalPaid()))
            .setTotalInvoiced(amount(installment.getTotalInvoiced()));
    }

    @Override
    protected Installment doBackward(InstallmentInfo installmentInfo) {
        throw new UnsupportedOperationException();
    }
}
