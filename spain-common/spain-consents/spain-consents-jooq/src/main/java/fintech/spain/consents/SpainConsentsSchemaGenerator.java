package fintech.spain.consents;

import fintech.bo.db.JooqGenerator;

public class SpainConsentsSchemaGenerator {

    public static void main(String[] args) throws Exception {
        new JooqGenerator("./spain-common/spain-consents/spain-consents-jooq/src/main/java", "spain_consents", "fintech.spain.consents.db.jooq").run();
    }

}
