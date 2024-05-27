package fintech.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SystemEnvironment {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SystemEnvironment(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static boolean isProd(JdbcTemplate jdbcTemplate) {
        // check if table 'prod' exists in public schema
        String isProd = jdbcTemplate.queryForObject("SELECT to_regclass('prod')", String.class);
        return isProd != null;
    }

    public boolean isProd() {
        return isProd(jdbcTemplate);
    }
}
