package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.payments.DisbursementService;
import fintech.payments.impl.UnnaxDisbursementProcessorBean;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.settigs.PaymentsSettings;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static fintech.payments.settigs.PaymentsSettingsService.PAYMENT_SETTINGS;
import static fintech.workflow.spi.ActivityResult.resolution;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class ExportDisbursementActivity implements ActivityHandler {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    UnnaxDisbursementProcessorBean disbursementProcessorBean;

    @Autowired
    DisbursementService disbursementService;

    private static final ActivityResult OK_RESULT = resolution(Resolutions.OK, "Disbursement exported");
    private static final ActivityResult SKIP_RESULT = resolution(Resolutions.SKIP, "Can't automatically export disbursement");


    @Override
    public ActivityResult handle(ActivityContext context) {
        return context.getAttribute(Attributes.DISBURSEMENT_ID)
            .map(Long::valueOf)
            .map(this::exportDisbursementIfAutoExportEnabled)
            .orElse(SKIP_RESULT);
    }

    private ActivityResult exportDisbursementIfAutoExportEnabled(long disbursementId) {
        PaymentsSettings settings = settingsService.getJson(PAYMENT_SETTINGS, PaymentsSettings.class);
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);

        DisbursementExportResult exportResult = Optional.of(disbursement)
            .filter(d -> d.isApiExport() && settings.isAutoExportEnabled())
            .map(d -> disbursementProcessorBean.exportSingleDisbursement(d.getId()))
            .orElse(DisbursementExportResult.empty());

        if (exportResult.isExported())
            return OK_RESULT;
        else
            return SKIP_RESULT;
    }
}
