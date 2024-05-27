package fintech.bo.spain.db;


import fintech.bo.db.JooqGenerator;

public class ExperianSchemaGenerator {

    public static void main(String[] args) throws Exception {
        new JooqGenerator("./spain-common/spain-experian/spain-experian-jooq/src/main/java", "spain_experian", "fintech.bo.spain.db.jooq.experian").run();
    }

}
