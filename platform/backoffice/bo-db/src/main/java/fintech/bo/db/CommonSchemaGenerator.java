package fintech.bo.db;


public class CommonSchemaGenerator {

    public static void main(String[] args) throws Exception {
        generateSchema("crm");
//        generateSchema("backoffice");
//        generateSchema("settings");
//        generateSchema("sms");
//        generateSchema("email");
//        generateSchema("cms");
//        generateSchema("payment");
//        generateSchema("security");
//        generateSchema("transaction");
//        generateSchema("task");
//        generateSchema("workflow");
//        generateSchema("rule");
//        generateSchema("lending");
//        generateSchema("accounting");
//        generateSchema("storage");
//        generateSchema("checklist");
//        generateSchema("nordigen");
//        generateSchema("instantor");
//        generateSchema("affiliate");
//        generateSchema("iovation");
//        generateSchema("quartz");
//        generateSchema("web_analytics");
//        generateSchema("viventor");
//        generateSchema("dc");
//        generateSchema("admin_tools");
//        generateSchema("activity");
    }

    private static void generateSchema(String schema) throws Exception {
        new JooqGenerator("./platform/backoffice/bo-db/src/main/java", schema, "fintech.bo.db.jooq." + schema).run();
    }
}
