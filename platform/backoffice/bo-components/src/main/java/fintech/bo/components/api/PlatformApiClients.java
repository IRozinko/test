package fintech.bo.components.api;

import fintech.bo.api.client.AccountingApiClient;
import fintech.bo.api.client.ActivityApiClient;
import fintech.bo.api.client.AdminToolsApiClient;
import fintech.bo.api.client.AffiliateApiClient;
import fintech.bo.api.client.AgentsApiClient;
import fintech.bo.api.client.AttachmentApiClient;
import fintech.bo.api.client.BlacklistApiClient;
import fintech.bo.api.client.CalendarApiClient;
import fintech.bo.api.client.ChecklistApiClient;
import fintech.bo.api.client.ClientEventApi;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.client.DisbursementApiClient;
import fintech.bo.api.client.DiscountApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.client.InvoiceApiClient;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.client.LoginApiClient;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.client.PeriodsApiClient;
import fintech.bo.api.client.ProductsApiClient;
import fintech.bo.api.client.PromoCodeApiClient;
import fintech.bo.api.client.QuartzApiClient;
import fintech.bo.api.client.ReportsApiClient;
import fintech.bo.api.client.RolesApiClient;
import fintech.bo.api.client.SettingsApiClient;
import fintech.bo.api.client.StatementApiClient;
import fintech.bo.api.client.StrategyApiClient;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.api.client.TransactionApiClient;
import fintech.bo.api.client.UsersApiClient;
import fintech.bo.api.client.WorkflowApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class PlatformApiClients {

    @Autowired
    private Retrofit retrofit;

    @Bean
    ClientEventApi clientEventApi() {
        return retrofit.create(ClientEventApi.class);
    }

    @Bean
    LoginApiClient googleLoginApiClient() {
        return retrofit.create(LoginApiClient.class);
    }

    @Bean
    SettingsApiClient settingsApiClient() {
        return retrofit.create(SettingsApiClient.class);
    }

    @Bean
    TaskApiClient taskApiClient() {
        return retrofit.create(TaskApiClient.class);
    }

    @Bean
    PaymentApiClient paymentApiClient() {
        return retrofit.create(PaymentApiClient.class);
    }

    @Bean
    InvoiceApiClient invoiceApiClient() {
        return retrofit.create(InvoiceApiClient.class);
    }

    @Bean
    LoanApiClient loanApiClient() {
        return retrofit.create(LoanApiClient.class);
    }

    @Bean
    RolesApiClient rolesApiClient() {
        return retrofit.create(RolesApiClient.class);
    }

    @Bean
    UsersApiClient userApiClient() {
        return retrofit.create(UsersApiClient.class);
    }

    @Bean
    StrategyApiClient strategyApiClient() {
        return retrofit.create(StrategyApiClient.class);
    }

    @Bean
    AgentsApiClient agentsApiClient() {
        return retrofit.create(AgentsApiClient.class);
    }

    @Bean
    BlacklistApiClient blacklistApiClient() {
        return retrofit.create(BlacklistApiClient.class);
    }

    @Bean
    CalendarApiClient calendarApiClient() {
        return retrofit.create(CalendarApiClient.class);
    }

    @Bean
    DisbursementApiClient disbursementApiClient() {
        return retrofit.create(DisbursementApiClient.class);
    }

    @Bean
    DiscountApiClient discountApiClient() {
        return retrofit.create(DiscountApiClient.class);
    }

    @Bean
    FileApiClient fileApiClient() {
        return retrofit.create(FileApiClient.class);
    }

    @Bean
    StatementApiClient statementApiClient() {
        return retrofit.create(StatementApiClient.class);
    }

    @Bean
    WorkflowApiClient workflowApiClient() {
        return retrofit.create(WorkflowApiClient.class);
    }

    @Bean
    CmsApiClient cmsApiClient() {
        return retrofit.create(CmsApiClient.class);
    }

    @Bean
    TransactionApiClient transactionApiClient() {
        return retrofit.create(TransactionApiClient.class);
    }

    @Bean
    AttachmentApiClient attachmentApiClient() {
        return retrofit.create(AttachmentApiClient.class);
    }

    @Bean
    PeriodsApiClient periodsApiClient() {
        return retrofit.create(PeriodsApiClient.class);
    }

    @Bean
    AccountingApiClient accountingApiClient() {
        return retrofit.create(AccountingApiClient.class);
    }

    @Bean
    ChecklistApiClient checklistApiClient() {
        return retrofit.create(ChecklistApiClient.class);
    }

    @Bean
    ReportsApiClient reportsApiClient() {
        return retrofit.create(ReportsApiClient.class);
    }

    @Bean
    AffiliateApiClient affiliateApiClient() {
        return retrofit.create(AffiliateApiClient.class);
    }

    @Bean
    ProductsApiClient productsApiClient() {
        return retrofit.create(ProductsApiClient.class);
    }

    @Bean
    PromoCodeApiClient promoCodeApiClient() {
        return retrofit.create(PromoCodeApiClient.class);
    }

    @Bean
    DcApiClient dcApiClient() {
        return retrofit.create(DcApiClient.class);
    }

    @Bean
    AdminToolsApiClient adminToolsApiClient() {
        return retrofit.create(AdminToolsApiClient.class);
    }

    @Bean
    ActivityApiClient activityApiClient() {
        return retrofit.create(ActivityApiClient.class);
    }

    @Bean
    QuartzApiClient quartzApiClient() {
        return retrofit.create(QuartzApiClient.class);
    }
}
