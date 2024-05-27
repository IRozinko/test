package fintech.spain.platform.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SpecialLinkActivated {
    SpecialLink link;
    Map<String, Object> activationParameters;
}
