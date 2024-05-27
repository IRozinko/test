package fintech.testing.integration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;


public class TestDatabaseFactory {

    private static TestDatabaseFactory instance;

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5433/itest";
    private static final String DB_USER = "itest";
    private static final String DB_PASSWORD = "itest";

    private final JdbcTemplate jdbcTemplate;
    private final HikariDataSource dataSource;
    private String dbName;

    public TestDatabaseFactory() {
        dataSource = dataSource();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createTestDb() {
        dbName = "itest_" + System.currentTimeMillis() + "_" + RandomStringUtils.randomAlphanumeric(3).toLowerCase();
        jdbcTemplate.execute(String.format("drop database if exists %s", dbName));
        jdbcTemplate.execute(String.format("create database %s owner itest template template_loc --encoding utf-8", dbName));
        System.setProperty("db.url", String.format("jdbc:postgresql://localhost:5433/%s", dbName));
        System.setProperty("db.user", DB_USER);
        System.setProperty("db.password", DB_PASSWORD);

        // make sure to drop test db on JVM exit (basically when all tests are completed)
        Runtime.getRuntime().addShutdownHook(new Thread(this::dropTestDb));
    }

    public void dropTestDb() {
        try {
            if (dbName != null) {
                jdbcTemplate.execute(String.format("drop database if exists %s", dbName));
            }
        } finally {
            if(dataSource != null) {
               dataSource.close();
            }
        }
    }

    public static TestDatabaseFactory get() {
        if (instance == null) {
            instance = new TestDatabaseFactory();
        }
        return instance;
    }

    private static HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(jdbcUrl());
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(1);
        return new HikariDataSource(config);
    }

    private static String jdbcUrl() {
        return System.getProperty("db.url", JDBC_URL);
    }

    public static void generateDdlWithHibernateToStdout(Class... classes) throws SQLException {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.dialect", "fintech.db.impl.CustomPostgreSqlDialect")
            .applySetting("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy")
            .applySetting("org.hibernate.envers.audit_table_suffix", "_audit")
            .applySetting("org.hibernate.envers.store_data_at_delete", "true")
            .build();

        MetadataSources metadataSources = new MetadataSources(registry);
        Arrays.stream(classes).forEach(metadataSources::addAnnotatedClass);
        Metadata metadata = metadataSources.buildMetadata();

        new SchemaExport()
            .setFormat(true)
            .setDelimiter(";")
            .create(EnumSet.of(TargetType.STDOUT), metadata);
    }

}
