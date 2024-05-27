package fintech.spain.alfa.bo.api;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.strategy.CreateStrategyRequest;
import fintech.bo.api.model.strategy.UpdateStrategyRequest;
import fintech.bo.api.server.security.BackofficeUser;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.SaveCalculationStrategyCommand;
import fintech.strategy.UpdateCalculationStrategyCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class StrategyApiController {

    @Autowired
    private CalculationStrategyService service;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.STRATEGIES_EDIT})
    @PostMapping("/api/bo/strategy/create")
    public void createStrategy(@AuthenticationPrincipal BackofficeUser user, @Valid @RequestBody CreateStrategyRequest request) {
        SaveCalculationStrategyCommand command = new SaveCalculationStrategyCommand()
            .setEnabled(request.isEnabled())
            .setDefault(request.isDefaultStrategy())
            .setStrategyType(request.getStrategyType())
            .setCalculationType(request.getCalculationType())
            .setVersion(request.getVersion())
            .setProperties(request.getProperties());

        Long id = service.saveCalculationStrategy(command);

        log.info("created new strategy [{}] with id [{}]", command, id);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.STRATEGIES_EDIT})
    @PostMapping("/api/bo/strategy/update")
    public void updateStrategy(@AuthenticationPrincipal BackofficeUser user, @Valid @RequestBody UpdateStrategyRequest request) {
        UpdateCalculationStrategyCommand command = new UpdateCalculationStrategyCommand()
            .setStrategyId(request.getStrategyId())
            .setEnabled(request.isEnabled())
            .setDefault(request.isDefaultStrategy())
            .setVersion(request.getVersion())
            .setProperties(request.getProperties());

        service.updateCalculationStrategy(command);

        log.info("updated strategy [{}]", command);
    }
}
