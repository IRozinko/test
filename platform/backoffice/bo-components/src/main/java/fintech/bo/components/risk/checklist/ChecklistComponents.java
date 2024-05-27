package fintech.bo.components.risk.checklist;


import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.db.jooq.checklist.Tables.CHECKLIST_TYPE;

@Component
public class ChecklistComponents {

    @Autowired
    private DSLContext db;

    public ChecklistDataProvider checklistDataProvider() {
        return new ChecklistDataProvider(db);
    }

    public List<String> types() {
        return db.select(CHECKLIST_TYPE.TYPE).from(CHECKLIST_TYPE).fetch(CHECKLIST_TYPE.TYPE);
    }
}
