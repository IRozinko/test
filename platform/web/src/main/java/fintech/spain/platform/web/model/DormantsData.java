package fintech.spain.platform.web.model;

import fintech.crm.client.model.DormantsClientData;
import fintech.lending.core.application.model.DormantsApplicationData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DormantsData {

    private DormantsClientData clientData;
    private DormantsApplicationData applicationData;
}
