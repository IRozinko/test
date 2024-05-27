package fintech.spain.platform.web.validations;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, Extended.class})
public interface ValidationSequence {
}
