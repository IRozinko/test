package fintech.bo.components.task;

import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Value
public class TaskData {

    boolean taskIsOpen;
    boolean taskAssignedToMe;

}
