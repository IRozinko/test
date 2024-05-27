package fintech.bo.spain.unnax;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static fintech.spain.unnax.db.jooq.Tables.CALLBACK;

@Component
public class Queries {

    @Autowired
    private DSLContext db;

    public Collection<String> callbackTypes() {
        return db.selectDistinct(CALLBACK.EVENT)
            .from(CALLBACK)
            .orderBy(CALLBACK.EVENT)
            .fetchInto(String.class);
    }
}
