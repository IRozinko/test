package fintech.bo.components.dc;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import fintech.Validate;
import fintech.bo.components.JsonUtils;
import fintech.bo.db.jooq.dc.tables.records.ActionRecord;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import fintech.bo.db.jooq.dc.tables.records.SettingsRecord;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static fintech.bo.db.jooq.crm.tables.PhoneContact.PHONE_CONTACT;
import static fintech.bo.db.jooq.dc.Tables.ACTION;
import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static fintech.bo.db.jooq.dc.Tables.SETTINGS;
import static fintech.bo.db.jooq.lending.Tables.LOAN;

@Component
public class DcQueries {

    private Supplier<DcSettingsJson> settingsSupplier = newSettingsSupplier();

    private Supplier<DcSettingsJson> newSettingsSupplier() {
        return Suppliers.memoizeWithExpiration(this::loadSettings, 10, TimeUnit.SECONDS);
    }

    @Autowired
    private DSLContext db;

    public DebtRecord findDebtById(Long debtId) {
        return db.selectFrom(DEBT).where(DEBT.ID.eq(debtId)).fetchOne();
    }

    public Result<DebtRecord> findDebtsByClientId(Long clientId) {
        return db.selectFrom(DEBT).where(DEBT.CLIENT_ID.eq(clientId)).fetch();
    }

    public Optional<Long> findDebtIdByPhone(String phone) {
        return Optional.ofNullable(
            db.selectDistinct(DEBT.ID,LOAN.ISSUE_DATE)
                .from(DEBT)
                .join(PHONE_CONTACT).on(DEBT.CLIENT_ID.eq(PHONE_CONTACT.CLIENT_ID))
                //join with loan because created_at/updated_at can not reflect latest entity
                //as caused by migration
                .join(LOAN).on(LOAN.ID.eq(DEBT.LOAN_ID))
                .where(PHONE_CONTACT.LOCAL_NUMBER.eq(phone))
                .orderBy(LOAN.ISSUE_DATE.desc())
                .limit(1)
                .fetchOne())
            .map(Record2::value1);
    }

    public ActionRecord findActionById(Long actionId) {
        return db.selectFrom(ACTION).where(ACTION.ID.eq(actionId)).fetchOne();
    }

    public DcSettingsJson getSettings() {
        return settingsSupplier.get();
    }

    public String getRawSettings() {
        SettingsRecord record = db.selectFrom(SETTINGS).fetchOne();
        return record.getSettingsJson();
    }

    public void flushSettingsCache() {
        settingsSupplier = newSettingsSupplier();
    }

    private DcSettingsJson loadSettings() {
        SettingsRecord record = db.selectFrom(SETTINGS).fetchOne();
        Validate.notNull(record, "DC settings not found");
        String json = record.getSettingsJson();
        return JsonUtils.readValue(json, DcSettingsJson.class);
    }

    public List<String> listDebtAgents() {
        List<String> agents = db.selectDistinct(DEBT.AGENT).from(DEBT).where(DEBT.AGENT.isNotNull()).orderBy(DEBT.AGENT).fetchInto(String.class);
        return agents;
    }

    public List<String> listStatuses() {
        List<String> agents = db.selectDistinct(DEBT.STATUS).from(DEBT).where(DEBT.STATUS.isNotNull()).orderBy(DEBT.STATUS).fetchInto(String.class);
        return agents;
    }

    public List<String> listSubStatuses() {
        return db.selectDistinct(DEBT.SUB_STATUS).from(DEBT).where(DEBT.SUB_STATUS.isNotNull()).orderBy(DEBT.SUB_STATUS).fetchInto(String.class);
    }
}
