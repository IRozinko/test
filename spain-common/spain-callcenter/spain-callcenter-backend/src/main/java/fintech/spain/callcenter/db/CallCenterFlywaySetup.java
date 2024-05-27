package fintech.spain.callcenter.db;

import fintech.db.FlywayFactory;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class CallCenterFlywaySetup {

    @DependsOn({"flyway.common", "flyway.crm", "flyway.presence"})
    @Bean(name = "flyway.spain_callcenter", initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return FlywayFactory.create(dataSource, Entities.SCHEMA);
    }
}
