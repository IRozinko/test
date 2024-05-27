package fintech.workflow.spi;

import fintech.Validate;
import lombok.Getter;

@Getter
public class ActivityResult {

    private String resolution;
    private String resolutionDetail;
    private boolean fail;
    private String error;


    public static ActivityResult resolution(String resolution, String resolutionDetail) {
        ActivityResult result = new ActivityResult();
        result.resolution = Validate.notBlank(resolution);
        result.resolutionDetail = Validate.notNull(resolutionDetail);
        return result;
    }

    public static ActivityResult fail(String error) {
        ActivityResult result = new ActivityResult();
        result.error = Validate.notBlank(error);
        result.fail = true;
        return result;
    }
}
