package fintech.crm.db;

import fintech.db.FlywayFactory;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class CrmFlywaySetup {

    @DependsOn({"flyway.common"})
    @Bean(name = "flyway.crm", initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return FlywayFactory.create(dataSource, Entities.SCHEMA);
    }
}
