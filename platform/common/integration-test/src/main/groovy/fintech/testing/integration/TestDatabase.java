package fintech.testing.integration;

import com.google.common.collect.ImmutableSet;
import fintech.db.PostgreSqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class TestDatabase {

    private final JdbcTemplate jdbcTemplate;

    private List<String> tables;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    public TestDatabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void cleanDb() {
        cleanDb(ImmutableSet.of());
    }

    public void cleanDb(Collection<String> excludingTables) {
        txTemplate.execute(s -> {
            List<String> tableWithRows = tablesWithRows(excludingTables);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tableWithRows.forEach(t -> pw.println(String.format("alter table %s disable trigger all;", t)));
            tableWithRows.forEach(t -> pw.println(String.format("delete from %s;", t)));
            tableWithRows.forEach(t -> pw.println(String.format("alter table %s enable trigger all;", t)));
            jdbcTemplate.execute(sw.toString());
            return 0;
        });
    }


    private List<String> tablesWithRows(Collection<String> excludingTables) {
        List<String> tablesToClean = getTables().stream().filter(t -> !excludingTables.contains(t)).collect(Collectors.toList());
        if (tablesToClean.isEmpty()) {
            return new ArrayList<>();
        }
        return tablesToClean;
    }

    private List<String> getTables() {
        if (this.tables == null) {
            this.tables = PostgreSqlUtils.getTables(this.jdbcTemplate)
                .stream()
                .filter((t) -> !t.contains("schema_version") && !t.contains("_audit") && !t.equals("common.revision"))
                .collect(Collectors.toList());
        }
        return tables;
    }
}
