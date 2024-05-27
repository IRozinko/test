package fintech.bo.spain.alfa.db;


import fintech.bo.db.JooqGenerator;

public class AlfaSchemaGenerator {

    public static void main(String[] args) throws Exception {
        generateSchema("alfa", "alfa");
    }

    private static void generateSchema(String schema, String packageName) throws Exception {
        new JooqGenerator("./alfa/alfa-bo/src/main/java", schema, "fintech.bo.spain.alfa.db.jooq." + packageName).run();
    }
}
