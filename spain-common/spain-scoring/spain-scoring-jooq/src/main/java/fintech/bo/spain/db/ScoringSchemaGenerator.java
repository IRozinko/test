package fintech.bo.spain.db;


import fintech.bo.db.JooqGenerator;

public class ScoringSchemaGenerator {

    public static void main(String[] args) throws Exception {
        new JooqGenerator("./spain-common/spain-scoring/spain-scoring-jooq/src/main/java", "spain_scoring", "fintech.bo.spain.db.jooq.scoring").run();
    }

}
