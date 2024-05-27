package fintech.spain.alfa.app.admintools;

import fintech.JsonUtils;
import fintech.admintools.AdminAction;
import fintech.admintools.AdminActionContext;
import fintech.presence.PresenceException;
import fintech.presence.PresenceOutboundLoadNotAvailable;
import fintech.presence.impl.PresenceServiceBean;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SyncPresenceAdminAction implements AdminAction {

    private final PresenceServiceBean presenceServiceBean;

    @Autowired
    public SyncPresenceAdminAction(PresenceServiceBean presenceServiceBean) {
        this.presenceServiceBean = presenceServiceBean;
    }

    @Override
    public String getName() {
        return "SyncPresence";
    }

    @Override
    public void execute(AdminActionContext context) {
        SyncPresenceRequest request = JsonUtils.readValue(context.getParams(), SyncPresenceRequest.class);
        try {
            presenceServiceBean.updateOutboundLoad(request.getServiceId(), request.getLoadId());
            context.updateProgress("Completed");
        } catch (IOException | PresenceException | PresenceOutboundLoadNotAvailable e) {
            context.failed("Error " + e.getMessage());
        }
    }

    @Data
    private static class SyncPresenceRequest {

        private Integer serviceId;
        private Integer loadId;
    }
}
