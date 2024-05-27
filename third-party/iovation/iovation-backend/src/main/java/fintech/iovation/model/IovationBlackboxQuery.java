package fintech.iovation.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IovationBlackboxQuery {

    private Long clientId;
    private Long applicationId;

}
