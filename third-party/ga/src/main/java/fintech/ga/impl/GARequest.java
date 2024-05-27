package fintech.ga.impl;

import lombok.Value;

import java.util.Map;

@Value
public class GARequest {

    String serviceUrl;
    String userAgent;
    Map<String, String> parameters;

}
