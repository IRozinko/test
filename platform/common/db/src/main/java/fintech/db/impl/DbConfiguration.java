package fintech.db.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fintech.db.PostgreSqlUtils;
import fintech.db.SystemEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@Slf4j
@Configuration
@PropertySources(value = {@PropertySource(value = "classpath:db.properties")})
public class DbConfiguration {

    @Value("${db.url:jdbc:postgresql://localhost:5433/loc}")
    private String url;

    @Value("${db.user:fintech}")
    private String user;

    @Value("${db.password:fintech}")
    private String password;

    @Value("${db.reset:false}")
    private boolean reset = false;

    // depend on audit helper to always have audit data
    @DependsOn("auditInfoHelper")
    @Bean
    DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setIdleTimeout(60 * 1000);
        config.setMaxLifetime(5 * 60 * 1000);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(40);
        config.setConnectionTestQuery("select 1");
        config.setConnectionInitSql("set timezone TO 'UTC';");
        HikariDataSource dataSource = new HikariDataSource(config);
        if (reset) {
            resetDb(dataSource);
        }
        return dataSource;
    }

    private void resetDb(HikariDataSource dataSource) {
        log.warn("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\nRESETTING DATABASE\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        if (SystemEnvironment.isProd(jdbcTemplate)) {
            throw new IllegalStateException("Can not reset production database!");
        }
        PostgreSqlUtils.getSchemas(jdbcTemplate).forEach((schema) -> jdbcTemplate.execute("drop schema " + schema + " cascade"));
        PostgreSqlUtils.getTables(jdbcTemplate).forEach((table) -> jdbcTemplate.execute("drop table " + table + " cascade"));
        log.warn("Database reset completed");
    }
}
