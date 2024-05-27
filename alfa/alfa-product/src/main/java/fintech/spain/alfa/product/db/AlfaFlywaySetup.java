package fintech.spain.alfa.product.db;

import fintech.db.FlywayFactory;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
class AlfaFlywaySetup {

    @DependsOn({
        "flyway.crm",
        "flyway.calendar",
        "flyway.lending",
        "flyway.rule",
        "flyway.payment",
        "flyway.accounting",
        "flyway.notification",
        "flyway.cms",
        "flyway.security",
        "flyway.task",
        "flyway.workflow",
        "flyway.storage",
        "flyway.settings",
        "flyway.checklist",
        "flyway.affiliate",
        "flyway.web_analytics",
        "flyway.dc",
        "flyway.activity",
        "flyway.viventor",
        "flyway.nordigen",
        "flyway.instantor",
        "flyway.iovation",
        "flyway.spain_scoring",
        "flyway.spain_inglobaly",
        "flyway.spain_experian",
        "flyway.spain_equifax",
        "flyway.spain_asnef",
        "flyway.spain_crosscheck",
        "flyway.spain_callcenter",
        "flyway.strategy",
        "flyway.marketing"
    })
    @Primary
    @Bean(name = "flyway.alfa", initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return FlywayFactory.create(dataSource, Entities.SCHEMA);
    }
}
