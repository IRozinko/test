package fintech.bo.spain.db;


import fintech.bo.db.JooqGenerator;

public class EquifaxSchemaGenerator {

    public static void main(String[] args) throws Exception {
        new JooqGenerator("./spain-common/spain-equifax/spain-equifax-jooq/src/main/java", "spain_equifax", "fintech.bo.spain.db.jooq.equifax").run();
    }

}
