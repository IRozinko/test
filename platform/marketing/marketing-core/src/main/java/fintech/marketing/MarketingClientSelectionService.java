package fintech.marketing;

import com.google.common.base.Stopwatch;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import fintech.TimeMachine;
import fintech.crm.client.db.QClientEntity;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.db.QLoanEntity;
import fintech.marketing.MarketingAudienceSettings.AudienceCondition;
import fintech.marketing.predicate.PredicateResolver;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static fintech.crm.db.Entities.client;
import static fintech.crm.db.Entities.emailContact;
import static fintech.lending.core.application.LoanApplicationStatusDetail.APPROVED;
import static fintech.lending.core.application.LoanApplicationStatusDetail.CANCELLED;
import static fintech.lending.core.application.LoanApplicationStatusDetail.REJECTED;
import static fintech.lending.core.db.Entities.loan;
import static fintech.lending.core.db.Entities.loanApplication;

@Slf4j
@RequiredArgsConstructor
@Service
public class MarketingClientSelectionService {

    private final MarketingRegistry marketingRegistry;
    private final FileStorageService fileStorageService;
    private final JPQLQueryFactory queryFactory;

    public List<Long> getClientsSelection(List<AudienceCondition> audienceConditions, Optional<Duration> excludeWithDisbursementsWithin) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            log.info("Starting audience selection");

            List<Predicate> predicates = new ArrayList<>();

            QClientEntity clientEntity = new QClientEntity("client");
            predicates.add(clientEntity.acceptMarketing.isTrue());
            predicates.add(clientEntity.blockCommunication.isFalse());

            for (MarketingAudienceSettings.AudienceCondition c : audienceConditions) {
                MarketingConditionContext ctx = new MarketingConditionContextImpl(c, clientEntity);
                PredicateResolver resolver = marketingRegistry.getPredicateResolver(c.getType());
                predicates.add(resolver.resolve(ctx));
            }

            excludeWithDisbursementsWithin.ifPresent(duration -> predicates.add(
                JPAExpressions.selectFrom(loanApplication).where(
                    loanApplication.statusDetail.eq(APPROVED).and(loanApplication.clientId.eq(clientEntity.id))
                        .and(loanApplication.closeDate.goe(TimeMachine.now().minus(duration).toLocalDate()))
                ).notExists()
            ));

            return queryFactory.select(clientEntity.id)
                .from(clientEntity)
                .where(ExpressionUtils.allOf(predicates))
                .orderBy(clientEntity.id.asc())
                .fetch();
        } finally {
            log.info("End audience selection in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @SneakyThrows
    public CloudFile exportAudiencePreview(MarketingAudienceSettings campaignSettings) {

        List<Long> clients = getClientsSelection(campaignSettings.getAudienceConditions(), Optional.empty());
        File tempFile = null;
        CsvListWriter writer;
        String[] header = new String[]{"id", "firstName", "lastName", "email", "lastLoanCloseDate", "lastLoanDpd", "cancellations30d", "rejections30d", "paidLoans"};

        try {
            tempFile = File.createTempFile("AudiencePreview", "export");
            writer = new CsvListWriter(new FileWriter(tempFile), CsvPreference.EXCEL_PREFERENCE);
            writer.writeHeader(header);
            QLoanEntity l = new QLoanEntity("l");
            JPQLQuery<Tuple> query = queryFactory
                .select(
                    client.id,
                    client.firstName,
                    client.lastName,
                    emailContact.email.stringValue().coalesce("-"),
                    loan.closeDate.stringValue().coalesce("-"),
                    loan.overdueDays.stringValue().coalesce("-"),

                    JPAExpressions.select(loanApplication.count()).from(loanApplication)
                        .where(loanApplication.statusDetail.eq(CANCELLED)
                            .and(loanApplication.clientId.eq(client.id))
                            .and(loanApplication.closeDate.goe(TimeMachine.today().minusDays(30)))),

                    JPAExpressions.select(loanApplication.count()).from(loanApplication)
                        .where(loanApplication.statusDetail.eq(REJECTED)
                            .and(loanApplication.clientId.eq(client.id))
                            .and(loanApplication.closeDate.goe(TimeMachine.today().minusDays(30)))),

                    JPAExpressions.select(loan.count()).from(loan)
                        .where(loan.statusDetail.eq(LoanStatusDetail.PAID).and(loan.clientId.eq(client.id)))
                )
                .from(client)
                .leftJoin(emailContact).on(emailContact.client.id.eq(client.id).and(emailContact.primary.isTrue()))
                .leftJoin(loan).on(loan.id.eq(
                    JPAExpressions.select(l.id.max()).from(l).where(l.clientId.eq(client.id).and(l.closeDate.isNotNull()))
                ))
                .where(client.id.in(clients));
            try (CloseableIterator<Tuple> iterator = query.iterate()) {
                while (iterator.hasNext()) {
                    Tuple tuple = iterator.next();
                    writer.write(tuple.toArray());
                }
            }
            writer.close();

            @Cleanup FileInputStream fis = new FileInputStream(tempFile);
            SaveFileCommand saveFileCommand = new SaveFileCommand();
            saveFileCommand.setDirectory("AudiencePreview");
            saveFileCommand.setOriginalFileName("AudiencePreview.csv");
            saveFileCommand.setContentType("application/csv");
            saveFileCommand.setInputStream(fis);
            return fileStorageService.save(saveFileCommand);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

}
