package fintech.bo.components.cms;

import fintech.bo.db.jooq.cms.tables.records.ItemRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.db.jooq.cms.Tables.ITEM;

@Component
public class CmsQueries {

    @Autowired
    private DSLContext db;

    public ItemRecord findById(Long id) {
        return db.selectFrom(ITEM).where(ITEM.ID.eq(id)).fetchOne();
    }

    public List<ItemRecord> findByType(String name) {
        return db.selectFrom(ITEM).where(ITEM.ITEM_TYPE.eq(name)).fetch();
    }
}
