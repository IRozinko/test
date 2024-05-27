package fintech.bo.api.server;

import fintech.bo.api.model.UpdatePropertyRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.settings.SettingsService;
import fintech.settings.commands.UpdatePropertyCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SettingsApiController {

    @Autowired
    private SettingsService settingsService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.SETTINGS_EDIT})
    @PostMapping(path = "/api/bo/settings")
    public void update(@RequestBody UpdatePropertyRequest request) {
        log.info("Updating property [{}]", request);

        UpdatePropertyCommand command = new UpdatePropertyCommand();
        command.setName(request.getName());
        command.setBooleanValue(request.getBooleanValue());
        command.setNumberValue(request.getNumberValue());
        command.setDecimalValue(request.getDecimalValue());
        command.setTextValue(request.getTextValue());
        command.setDateValue(request.getDateValue());
        command.setDateTimeValue(request.getDateTimeValue());

        settingsService.update(command);
    }
}
