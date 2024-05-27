package fintech.marketing.bo;

import fintech.bo.components.IdNameDetails;
import fintech.bo.db.jooq.marketing.tables.MarketingTemplate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class MarketingTemplateQueries {

    @Autowired
    private DSLContext db;

    public List<IdNameDetails> findAll() {
        return db.select(MarketingTemplate.MARKETING_TEMPLATE.ID, MarketingTemplate.MARKETING_TEMPLATE.NAME).from(MarketingTemplate.MARKETING_TEMPLATE).stream()
            .map(r -> new IdNameDetails().setId(r.value1()).setName(r.value2()))
            .collect(Collectors.toList());
    }
}
