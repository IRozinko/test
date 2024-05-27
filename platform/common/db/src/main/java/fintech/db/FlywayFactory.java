package fintech.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayFactory {

    public static Flyway create(DataSource dataSource, String schema) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:db/" + schema);
        flyway.setSchemas(schema);
        flyway.setGroup(true);
        flyway.setCleanDisabled(true);
        return flyway;
    }

}
