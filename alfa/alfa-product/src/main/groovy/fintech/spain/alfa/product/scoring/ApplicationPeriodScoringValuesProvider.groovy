package fintech.spain.alfa.product.scoring

import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationStatus
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Component

import javax.sql.DataSource

import static fintech.lending.core.application.LoanApplicationQuery.byClientId

@Component
class ApplicationPeriodScoringValuesProvider extends SqlScoringProvider {

    private static final String PREFIX = "application_period"

    private final LoanApplicationService applicationService
    private final String query = """
              select max(requested_period_count) as max,
                     min(requested_period_count) as min, 
                     avg(requested_period_count) as avg, 
                     coalesce(stddev(requested_period_count), 0) as std, 
                     (select requested_period_count 
                         from lending.loan_application app 
                         where client_id = :clientId and id != :applicationId
                         order by submitted_at desc limit 1) as last
              from lending.loan_application app 
              where client_id = :clientId and id != :applicationId;"""


    ApplicationPeriodScoringValuesProvider(LoanApplicationService applicationService, DataSource dataSource) {
        super(dataSource)
        this.applicationService = applicationService
    }

    @Override
    Properties provide(long clientId) {
        def application = applicationService.findFirst(byClientId(clientId, LoanApplicationStatus.OPEN))
        return application.map({
            jdbcTemplate.queryForObject(query,
                new MapSqlParameterSource()
                    .addValue("clientId", clientId)
                    .addValue("applicationId", it.id),
                new SqlScoringProvider.ScoringRowMapper(PREFIX))
        }).orElse(new Properties())
    }

}
