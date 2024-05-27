package fintech.db;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


public abstract class PostgreSqlUtils {


    public static List<String> getTables(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("SELECT schemaname || '.' || tablename FROM pg_tables  WHERE schemaname NOT IN ('pg_catalog', 'information_schema', 'migration_source')", String.class);
    }

    public static List<String> getSchemas(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("SELECT nspname FROM pg_catalog.pg_namespace WHERE nspname NOT IN ('pg_catalog', 'information_schema', 'public', 'migration_source') AND nspname NOT LIKE 'pg_toast%' AND nspname NOT LIKE 'pg_temp%'", String.class);
    }
}
