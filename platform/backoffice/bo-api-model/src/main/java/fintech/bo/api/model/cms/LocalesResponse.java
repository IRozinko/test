package fintech.bo.api.model.cms;

import lombok.Data;

import java.util.Map;

@Data
public class LocalesResponse {
    private Map<String, Integer> locales;
}
