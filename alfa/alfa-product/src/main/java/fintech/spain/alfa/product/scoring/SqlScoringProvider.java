package fintech.spain.alfa.product.scoring;

import fintech.BigDecimalUtils;
import fintech.ScoringProperties;
import fintech.scoring.values.spi.ScoringValuesProvider;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;


public abstract class SqlScoringProvider implements ScoringValuesProvider {

    protected final NamedParameterJdbcTemplate jdbcTemplate;
    protected final DataSource dataSource;

    public SqlScoringProvider(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static class ScoringRowMapper implements RowMapper<Properties> {

        private final String prefix;

        public ScoringRowMapper(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Properties mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScoringProperties props = new ScoringProperties(prefix);
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                BigDecimal value = rs.getBigDecimal(i);
                props.put(rs.getMetaData().getColumnLabel(i), amount(value));
            }
            return props;
        }

        private BigDecimal amount(BigDecimal val) {
            return Optional.ofNullable(val)
                .map(BigDecimalUtils::amount)
                .orElse(null);
        }
    }
}
