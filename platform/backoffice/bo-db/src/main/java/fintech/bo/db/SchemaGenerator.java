package fintech.bo.db;


import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.*;

public class SchemaGenerator {

    static class CustomGenerator {
        private final String inputSchema;
        private final String packageName;

        CustomGenerator(String inputSchema, String packageName) {
            this.inputSchema = inputSchema;
            this.packageName = packageName;
        }

        public void run() throws Exception {
            CustomType jsonbType = new CustomType();
            jsonbType.setName("com.fasterxml.jackson.databind.JsonNode");
            jsonbType.setConverter("fintech.bo.db.PostgresJsonNodeBinding");

            ForcedType forcedType = new ForcedType();
            forcedType.setExpression(".*json");
            forcedType.setTypes("jsonb");
            forcedType.setName("com.fasterxml.jackson.databind.JsonNode");

            Configuration configuration = new Configuration()
                .withJdbc(new Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUrl("jdbc:postgresql://localhost:5433/loc")
                    .withUser("fintech")
                    .withPassword("fintech"))
                .withGenerator(new Generator()
                    .withDatabase(new Database()
                        .withName("org.jooq.util.postgres.PostgresDatabase")
                        .withIncludes(".*")
                        .withExcludes("schema_version|.*_audit")
                        .withInputSchema(this.inputSchema)
                        .withCustomTypes(jsonbType)
                        .withForcedTypes(forcedType)
                    )
                    .withTarget(new Target()
                        .withPackageName(this.packageName)
                        .withDirectory("./platform/backoffice/bo-db/src/main/java"))
                    .withGenerate(new Generate()
                        .withJavaTimeTypes(true)
                        .withRecords(true))
                );
            GenerationTool.generate(configuration);
        }
    }

    public static void main(String[] args) throws Exception {
//        generateSchema("crm");
//        generateSchema("backoffice");
//        generateSchema("settings");
//        generateSchema("sms");
//        generateSchema("email");
//        generateSchema("notification");
//        generateSchema("cms");
//        generateSchema("payment");
//        generateSchema("security");
//        generateSchema("transaction");
//        generateSchema("task");
//        generateSchema("workflow");
//        generateSchema("rule");
//        generateSchema("lending");
//        generateSchema("strategy");
        generateSchema("dc");
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
        new CustomGenerator(schema, "fintech.bo.db.jooq." + schema).run();
    }
}
