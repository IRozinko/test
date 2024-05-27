package fintech.bo.spain.db;


import fintech.bo.db.JooqGenerator;

public class SpainAsnefSchemaGenerator {

    public static void main(String[] args) throws Exception {
        new JooqGenerator("./spain-common/spain-asnef/spain-asnef-jooq/src/main/java", "spain_asnef", "fintech.bo.spain.db.jooq.asnef").run();
    }

}
