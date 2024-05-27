package fintech.webitel.impl;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Data
@Accessors(chain = true)
class OriginateNewCallRequest {
    private String calledId;
    private String callerId;
    private String autoAnswerParam;
}
