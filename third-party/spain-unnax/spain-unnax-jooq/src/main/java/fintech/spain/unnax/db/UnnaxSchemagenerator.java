package fintech.spain.unnax.db;

import fintech.bo.db.JooqGenerator;

public class UnnaxSchemagenerator {

    public static void main(String[] args) throws Exception {
        new JooqGenerator("./third-party/spain-unnax/spain-unnax-jooq/src/main/java", "spain_unnax", "fintech.spain.unnax.db.jooq").run();
    }

}
