//package fintech.spain.alfa.product.lending.impl;
//
//import com.google.common.collect.ImmutableMap;
//import com.querydsl.core.types.dsl.Expressions;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import fintech.lending.core.application.LoanApplication;
//import fintech.lending.core.application.LoanApplicationStatus;
//import fintech.lending.core.application.LoanApplicationStatusDetail;
//import fintech.lending.core.application.LoanApplicationType;
//import fintech.lending.core.application.db.LoanApplicationEntity;
//import fintech.lending.core.application.db.QLoanApplicationEntity;
//import fintech.lending.core.loan.LoanQuery;
//import fintech.lending.core.loan.LoanService;
//import fintech.settings.SettingsService;
//import fintech.spain.alfa.product.lending.ExpiredLoanApplicationNotificationService;
//import fintech.spain.alfa.product.lending.ExpiredLoanApplicationReminderSettings;
//import fintech.spain.alfa.product.settings.AlfaSettings;
//import fintech.spain.alfa.product.cms.AlfaCmsModels;
//import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
//import fintech.workflow.WorkflowStatus;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static fintech.crm.client.db.QClientEntity.clientEntity;
//import static fintech.lending.core.application.db.QLoanApplicationEntity.loanApplicationEntity;
//import static fintech.notification.db.QNotificationEntity.notificationEntity;
//import static fintech.spain.alfa.product.cms.CmsSetup.DROPOUT_NOTIFICATION;
//import static fintech.spain.alfa.product.cms.AlfaCmsModels.SCOPE_APPLICATION;
//import static fintech.spain.alfa.product.cms.AlfaCmsModels.SCOPE_AUTO_LOGIN;
//import static fintech.workflow.db.QWorkflowEntity.workflowEntity;
//
//@Component
//public class ExpiredLoanApplicationNotificationServiceBean implements ExpiredLoanApplicationNotificationService {
//
//    private final JPAQueryFactory queryFactory;
//    private final SettingsService settingsService;
//    private final AlfaNotificationBuilderFactory notificationFactory;
//    private final AlfaCmsModels cmsModels;
//    private final LoanService loanService;
//
//    public ExpiredLoanApplicationNotificationServiceBean(JPAQueryFactory queryFactory, SettingsService settingsService,
//                                                         AlfaNotificationBuilderFactory notificationFactory,
//                                                         AlfaCmsModels cmsModels, LoanService loanService) {
//        this.queryFactory = queryFactory;
//        this.settingsService = settingsService;
//        this.notificationFactory = notificationFactory;
//        this.cmsModels = cmsModels;
//        this.loanService = loanService;
//    }
//
////    @Override
////    public List<LoanApplication> getExpiredLoanApplications(LocalDateTime when) {
////        ExpiredLoanApplicationReminderSettings settings = settingsService.getJson(AlfaSettings.EXPIRED_APPLICATION_REMINDER_SETTINGS, ExpiredLoanApplicationReminderSettings.class);
////
////        QLoanApplicationEntity loanApplicationEntity2 = new QLoanApplicationEntity("loanApplicationEntity2");
////
////        List<LoanApplicationEntity> loanApplicationEntities = queryFactory.select(loanApplicationEntity)
////            .from(loanApplicationEntity)
////            .leftJoin(loanApplicationEntity2).on(loanApplicationEntity.clientId.eq(loanApplicationEntity2.clientId)
////                .and(loanApplicationEntity2.submittedAt.after(loanApplicationEntity.submittedAt))
////                .and(loanApplicationEntity2.type.eq(loanApplicationEntity.type)))
////            .join(workflowEntity).on(loanApplicationEntity.workflowId.eq(workflowEntity.id))
////            .join(clientEntity).on(loanApplicationEntity.clientId.eq(clientEntity.id))
////            .leftJoin(notificationEntity).on(clientEntity.id.eq(notificationEntity.clientId)
////                .and(notificationEntity.cmsKey.eq(DROPOUT_NOTIFICATION)))
////            .where(
////                Expressions.allOf(
////                    clientEntity.deleted.isFalse(),
////                    loanApplicationEntity.type.eq(LoanApplicationType.NEW_LOAN),
////                    loanApplicationEntity.status.eq(LoanApplicationStatus.CLOSED),
////                    loanApplicationEntity.statusDetail.eq(LoanApplicationStatusDetail.CANCELLED),
////                    workflowEntity.status.eq(WorkflowStatus.EXPIRED),
////                    workflowEntity.completedAt.before(when.minusHours(settings.getHours())),
////                    clientEntity.acceptMarketing.isTrue(),
////                    notificationEntity.id.isNull(),
////                    loanApplicationEntity2.isNull()))
////            .fetch();
////
////
////        Stream<LoanApplicationEntity> stream = loanApplicationEntities.stream();
////        if (!settings.isNewClients()) {
////            stream = stream.filter(lae -> !loanService.findLoans(LoanQuery.nonVoidedLoans(lae.getClientId())).isEmpty());
////        }
////        if (!settings.isRepeatedClients()) {
////            stream = stream.filter(lae -> loanService.findLoans(LoanQuery.nonVoidedLoans(lae.getClientId())).isEmpty());
////        }
////
////        return stream
////            .map(LoanApplicationEntity::toValueObject)
////            .collect(Collectors.toList());
////    }
//
//    @Override
//    public void sendNotification(LoanApplication loanApplication) {
//        notificationFactory.fromCustomerService(loanApplication.getClientId())
//            .render(DROPOUT_NOTIFICATION,
//                ImmutableMap.of(SCOPE_APPLICATION, cmsModels.application(loanApplication.getId()),
//                    SCOPE_AUTO_LOGIN, cmsModels.autoLogin(loanApplication.getClientId())))
//            .send();
//    }
//}
