package fintech.spain.dc.util.impl;

import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.ScheduleService;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.spain.dc.util.InstallmentNumberGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class InstallmentNumberGeneratorBean implements InstallmentNumberGenerator {

    private final ScheduleService scheduleService;

    @Autowired
    public InstallmentNumberGeneratorBean(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public String generate(Loan loan, ReschedulingPreview.Item item) {
        String number = format("%s-F%s", loan.getNumber(), StringUtils.leftPad(String.valueOf(item.getInstallmentSequence()), 2, '0'));
        for (int i = 0; i < 100; i++) {
            if (!scheduleService.findInstallmentByNumber(number).isPresent()) {
                return number;
            }
            number = format("%s-F%s-%s", loan.getNumber(), StringUtils.leftPad(String.valueOf(item.getInstallmentSequence()), 2, '0'), i + 1);
        }
        throw new IllegalStateException("Could not generate unique installment number");
    }
}
