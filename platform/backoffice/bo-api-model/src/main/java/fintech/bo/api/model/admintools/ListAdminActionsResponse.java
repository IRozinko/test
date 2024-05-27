package fintech.bo.api.model.admintools;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListAdminActionsResponse {

    private List<String> actions = new ArrayList<>();
}
