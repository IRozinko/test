package fintech.risk.checklist;

import fintech.risk.checklist.db.CheckListTypeEntity;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckListEntry {

    private Long id;
    private CheckListTypeEntity type;
    private String value1;
    private String comment;

}
