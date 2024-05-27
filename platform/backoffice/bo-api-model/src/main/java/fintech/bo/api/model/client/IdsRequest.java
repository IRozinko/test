package fintech.bo.api.model.client;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class IdsRequest {

    @NotNull
    List<Long> ids;
}
