package fintech.workflow.spi;

import lombok.Value;

@Value
public class ResolutionWithDetail {
    private String resolution;
    private String resolutionDetail;
}
