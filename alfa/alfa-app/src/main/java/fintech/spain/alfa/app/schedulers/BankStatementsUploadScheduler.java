package fintech.spain.alfa.app.schedulers;

import fintech.TimeMachine;
import fintech.quartz.QuartzConfig;
import fintech.spain.unnax.UnnaxBankStatementsService;
import fintech.spain.unnax.UnnaxPayOutService;
import fintech.spain.unnax.db.TransferAutoEntity;
import fintech.spain.unnax.db.TransferAutoStatus;
import fintech.spain.unnax.model.TransferAutoQuery;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@DisallowConcurrentExecution
public class BankStatementsUploadScheduler implements Job {

    @Autowired
    private UnnaxBankStatementsService statementsService;

    @Autowired
    private UnnaxPayOutService unnaxPayOutService;

    @Override
    public void execute(JobExecutionContext context) {
        Map<String, LocalDate> lastRequestedDateByIban = statementsService.lastSuccessRequestedDateByIban();

        LocalDate minDate = lastRequestedDateByIban.values().stream()
            .min(LocalDate::compareTo)
            .orElse(TimeMachine.today().minusDays(7));

        LocalDate requestDateTo = TimeMachine.today().minusDays(2);

        TransferAutoQuery query = new TransferAutoQuery();
        query.setStatus(TransferAutoStatus.PROCESSED);
        query.setProcessedFromDate(minDate.plusDays(1).atStartOfDay());
        query.setProcessedToDate(requestDateTo.plusDays(1).atStartOfDay());

        Set<String> usedSourceIbans = unnaxPayOutService.findTransferOuts(query).stream()
            .map(TransferAutoEntity::getSourceAccount)
            .collect(Collectors.toSet());

        for (String iban : usedSourceIbans) {
            LocalDate requestDateFrom = lastRequestedDateByIban.containsKey(iban) ? lastRequestedDateByIban.get(iban).plusDays(1) : requestDateTo;
            if (!requestDateFrom.isAfter(requestDateTo)) {
                statementsService.requestStatementsUpload(requestDateFrom, requestDateTo, iban);
            }
        }
    }


    @Bean(name = "bankStatementsUploadSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("BankStatementsUploadScheduler")
            .build();
    }

    @Bean(name = "bankStatementsUploadSchedulerTrigger")
    public CronTriggerFactoryBean trigger(@Qualifier("bankStatementsUploadSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, "0 4 0 * * ?");
    }

}
