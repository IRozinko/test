package fintech.ga.impl;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GAProperties {

    @Value("${ga.serviceUrl:mockServer}")
    private String serviceUrl;

    @Value("${ga.trackingIdParamName:tid}")
    private String trackingIdParamName;

    @Value("${ga.clientIdParamName:cid}")
    private String clientIdParamName;

    @Value("${ga.trackingId:fakeTrackingId}")
    private String trackingId;

}
