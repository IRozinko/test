package fintech.bo.db;

import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.CustomType;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.ForcedType;
import org.jooq.util.jaxb.Generate;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Jdbc;
import org.jooq.util.jaxb.Target;

public class JooqGenerator {
    private final String directory;
    private final String inputSchema;
    private final String packageName;

    public JooqGenerator(String directory, String inputSchema, String packageName) {
        this.directory = directory;
        this.inputSchema = inputSchema;
        this.packageName = packageName;
    }

    public void run() throws Exception {
        CustomType jsonbType = new CustomType();
        jsonbType.setName("com.fasterxml.jackson.databind.JsonNode");
        jsonbType.setConverter("fintech.bo.db.PostgresJsonNodeBinding");

        ForcedType forcedType = new ForcedType();
        forcedType.setExpression(".*json");
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
                    .withDirectory(directory))
                .withGenerate(new Generate()
                    .withJavaTimeTypes(true)
                    .withRecords(true))
            );
        GenerationTool.generate(configuration);
    }
}
