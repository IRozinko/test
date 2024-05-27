package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.experian.model.CaisRequest;
import fintech.spain.experian.model.CaisResumenResponse;
import fintech.spain.experian.model.ExperianStatus;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class ExperianCaisResumenActivity extends AbstractExperianActivity {

    private String onFailureResolution;

    public ExperianCaisResumenActivity() {
    }

    public ExperianCaisResumenActivity(String onFailureResolution) {
        this.onFailureResolution = onFailureResolution;
    }

    @Override
    protected ActivityResult execute(ActivityContext context, CaisRequest request) {
        CaisResumenResponse response = experianService.requestResumen(request);
        if (response.getStatus() == ExperianStatus.ERROR) {
            return Optional.ofNullable(onFailureResolution).map(resolution -> ActivityResult.resolution(resolution, "")).orElseGet(() -> ActivityResult.fail("Experian CAIS resumen request failed"));
        } else {
            context.setAttribute(Attributes.EXPERIAN_CAIS_RESUMEN_RESPONSE_ID, response.getId().toString());
            return ActivityResult.resolution(Resolutions.OK, "");
        }
    }
}
