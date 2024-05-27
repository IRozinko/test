package fintech.bo.spain.db;


import fintech.bo.db.JooqGenerator;

public class InglobalySchemaGenerator {

    public static void main(String[] args) throws Exception {
        new JooqGenerator("./spain-common/spain-inglobaly/spain-inglobaly-jooq/src/main/java", "spain_inglobaly", "fintech.bo.spain.db.jooq.inglobaly").run();
    }

}
