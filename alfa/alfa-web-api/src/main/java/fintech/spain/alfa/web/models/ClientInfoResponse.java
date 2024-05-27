package fintech.spain.alfa.web.models;


import fintech.spain.alfa.product.web.model.PopupInfo;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@Slf4j
public class ClientInfoResponse {

    private boolean authenticated;
    private Long id;
    private String number;
    private String state;
    private Map<String, Object> data = new HashMap<>();
    private String documentNumber;
    private ApplicationInfo application;
    private List<PopupInfo> popups;
    private List<String> roles;
    private boolean temporaryPassword;
    private boolean qualifiedForNewLoan;
    private boolean transferredToLoc;

    public static ClientInfoResponse notAuthenticated() {
        ClientInfoResponse info = new ClientInfoResponse();
        info.authenticated = false;
        return info;
    }
}
