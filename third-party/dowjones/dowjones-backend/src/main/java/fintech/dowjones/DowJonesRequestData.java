package fintech.dowjones;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class DowJonesRequestData {

    private Long clientId;
    private Map<String, String> parameters;

}
