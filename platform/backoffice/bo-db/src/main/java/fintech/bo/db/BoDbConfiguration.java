package fintech.bo.db;

import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.jooq.ExecuteType.READ;

@Configuration
public class BoDbConfiguration {

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        transactionManager.setEnforceReadOnly(true);
        return transactionManager;
    }

    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(dataSource);
        org.jooq.Configuration config = new DefaultConfiguration().set(proxy).set(SQLDialect.POSTGRES).set(ensureReadOnlyAccess());
        return DSL.using(config);
    }

    private DefaultExecuteListenerProvider ensureReadOnlyAccess() {
        return new DefaultExecuteListenerProvider(new DefaultExecuteListener() {
            @Override
            public void start(ExecuteContext ctx) {
                if (ctx.type() != READ) {
                    throw new DataAccessException("No privilege to execute SQL");
                }
            }
        });
    }
}
