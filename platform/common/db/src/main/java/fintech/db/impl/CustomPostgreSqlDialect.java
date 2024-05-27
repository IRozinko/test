package fintech.db.impl;

import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

public class CustomPostgreSqlDialect extends PostgreSQL9Dialect {

    public CustomPostgreSqlDialect() {
        super();
        registerColumnType(Types.VARCHAR, "text");
    }

}
