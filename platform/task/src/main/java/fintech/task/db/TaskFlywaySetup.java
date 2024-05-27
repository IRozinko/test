package fintech.task.db;

import fintech.db.FlywayFactory;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class TaskFlywaySetup {

    @DependsOn({"flyway.common"})
    @Bean(name = "flyway.task", initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return FlywayFactory.create(dataSource, Entities.SCHEMA);
    }
}
