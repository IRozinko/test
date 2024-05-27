package fintech.bo.components.risk.checklist;

import fintech.bo.db.jooq.checklist.tables.records.ChecklistEntryRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static fintech.bo.db.jooq.checklist.tables.ChecklistEntry.CHECKLIST_ENTRY;

@Component
public class ChecklistQueries {

    @Autowired
    private DSLContext db;

    public Optional<ChecklistEntryRecord> findByTypeAndValue1(String type, String value1) {
        ChecklistEntryRecord record = db.selectFrom(CHECKLIST_ENTRY)
            .where(CHECKLIST_ENTRY.TYPE.eq(type).and(CHECKLIST_ENTRY.VALUE1.equalIgnoreCase(value1)))
            .fetchOne();

        return Optional.ofNullable(record);
    }
}
