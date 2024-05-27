package fintech.bo.spain.alfa.client;

import fintech.bo.components.client.dto.ClientDTO;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.checklist.tables.ChecklistEntry.CHECKLIST_ENTRY;

@Component
public class AlfaChecklistQueries {

    public static final String CHECKLIST_TYPE_DNI = "DNI";
    public static final String CHECKLIST_TYPE_EMAIL = "EMAIL";
    public static final String CHECKLIST_TYPE_PHONE = "PHONE";

    @Autowired
    private DSLContext db;

    public boolean isBlacklisted(ClientDTO client) {

        return db.selectCount().from(CHECKLIST_ENTRY)
            .where(
                CHECKLIST_ENTRY.TYPE.eq(CHECKLIST_TYPE_DNI).and(CHECKLIST_ENTRY.VALUE1.equalIgnoreCase(client.getDocumentNumber()))
                    .or(CHECKLIST_ENTRY.TYPE.eq(CHECKLIST_TYPE_EMAIL).and(CHECKLIST_ENTRY.VALUE1.equalIgnoreCase(client.getEmail())))
                    .or(CHECKLIST_ENTRY.TYPE.eq(CHECKLIST_TYPE_PHONE).and(CHECKLIST_ENTRY.VALUE1.equalIgnoreCase(client.getPhone())))
            ).fetchOne(0, int.class) > 0;
    }

}
