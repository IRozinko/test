package fintech.bo.api.server;

import com.google.common.collect.ImmutableList;
import fintech.JsonUtils;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.dc.AddAgentAbsenceRequest;
import fintech.bo.api.model.dc.AutoAssignDebtRequest;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.api.model.dc.RemoveAgentAbsenceRequest;
import fintech.bo.api.model.dc.SaveAgentRequest;
import fintech.bo.api.model.dc.SaveDcSettingsRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.server.security.BackofficeUser;
import fintech.dc.DcAgentService;
import fintech.dc.DcService;
import fintech.dc.DcSettingsService;
import fintech.dc.commands.AddAgentAbsenceCommand;
import fintech.dc.commands.AutoAssignDebtCommand;
import fintech.dc.commands.LogDebtActionCommand;
import fintech.dc.commands.RemoveAgentAbsenceCommand;
import fintech.dc.commands.SaveAgentCommand;
import fintech.dc.model.DcSettings;
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
public class DcApiController {

    private final DcService dcService;
    private final DcSettingsService dcSettingsService;
    private final DcAgentService dcAgentService;

    @Autowired
    public DcApiController(DcService dcService, DcSettingsService dcSettingsService, DcAgentService dcAgentService) {
        this.dcService = dcService;
        this.dcSettingsService = dcSettingsService;
        this.dcAgentService = dcAgentService;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_DEBT_EDIT})
    @PostMapping("/api/bo/dc/log-debt-action")
    public IdResponse logDebtAction(@AuthenticationPrincipal BackofficeUser user, @Valid @RequestBody LogDebtActionRequest request) {
        LogDebtActionCommand command = new LogDebtActionCommand();
        command.setDebtId(request.getDebtId());
        command.setActionName(request.getActionName());
        command.setComments(request.getComments());
        command.setAgent(user.getUsername());
        command.setStatus(request.getStatus());
        command.setSubStatus(request.getSubStatus());
        command.setResolution(request.getResolution());
        command.setNextActionAt(request.getNextActionAt());
        command.setNextAction(request.getNextAction());

        request.getBulkActions().forEach((k, v) -> {
            LogDebtActionCommand.BulkAction p = new LogDebtActionCommand.BulkAction();
            p.setParams(v.getParams());
            command.getBulkActions().put(k, p);
        });

        Long id = dcService.logAction(command);
        return new IdResponse(id);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_AGENT_EDIT})
    @PostMapping("/api/bo/dc/save-agent")
    public IdResponse saveAgent(@Valid @RequestBody SaveAgentRequest request) {
        SaveAgentCommand command = new SaveAgentCommand();
        command.setAgent(request.getAgent());
        command.setDisabled(request.isDisabled());
        command.setPortfolios(ImmutableList.copyOf(request.getPortfolios()));
        Long id = dcAgentService.saveAgent(command);
        return new IdResponse(id);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_AGENT_EDIT})
    @PostMapping("/api/bo/dc/add-agent-absence")
    public IdResponse addAgentAbsence(@Valid @RequestBody AddAgentAbsenceRequest request) {
        AddAgentAbsenceCommand command = new AddAgentAbsenceCommand();
        command.setAgent(request.getAgent());
        command.setDateFrom(request.getDateFrom());
        command.setDateTo(request.getDateTo());
        command.setReason(request.getReason());
        Long id = dcAgentService.addAgentAbsence(command);
        return new IdResponse(id);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_AGENT_EDIT})
    @PostMapping("/api/bo/dc/remove-agent-absence")
    public void removeAgentAbsence(@Valid @RequestBody RemoveAgentAbsenceRequest request) {
        RemoveAgentAbsenceCommand command = new RemoveAgentAbsenceCommand();
        command.setId(request.getId());
        dcAgentService.removeAgentAbsence(command);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_DEBT_ASSIGN})
    @PostMapping("/api/bo/dc/auto-assign-debt")
    public void autoAssignDebt(@Valid @RequestBody AutoAssignDebtRequest request) {
        AutoAssignDebtCommand command = new AutoAssignDebtCommand();
        command.setDebtId(request.getDebtId());
        command.setExcludeAgent(request.getExcludeAgent());
        dcService.autoAssignDebt(command);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_SETTINGS_EDIT})
    @PostMapping("/api/bo/dc/save-settings")
    public void saveSettings(@Valid @RequestBody SaveDcSettingsRequest request) {
        DcSettings settings = JsonUtils.readValue(request.getJson(), DcSettings.class);
        dcSettingsService.saveSettings(settings, true);
    }
}
