package fintech.affiliate.impl;

import com.google.common.collect.ImmutableList;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.affiliate.AffiliateService;
import fintech.affiliate.db.*;
import fintech.affiliate.model.*;
import fintech.affiliate.spi.AffiliateRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Transactional
@Component
public class AffiliateServiceBean implements AffiliateService {

    private static final PebbleEngine pebbleEngine = newEngine();

    private final AffiliatePartnerRepository partnerRepository;
    private final AffiliateLeadRepository leadRepository;
    private final AffiliateEventRepository eventRepository;
    private final AffiliateRequestRepository requestRepository;
    private final JPAQueryFactory queryFactory;
    private final AffiliateRegistry affiliateRegistry;

    @Autowired
    public AffiliateServiceBean(AffiliatePartnerRepository partnerRepository, AffiliateLeadRepository leadRepository,
                                AffiliateEventRepository eventRepository, AffiliateRequestRepository requestRepository,
                                JPAQueryFactory queryFactory, AffiliateRegistry affiliateRegistry) {
        this.partnerRepository = partnerRepository;
        this.leadRepository = leadRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.queryFactory = queryFactory;
        this.affiliateRegistry = affiliateRegistry;
    }

    private static PebbleEngine newEngine() {
        return new PebbleEngine.Builder().autoEscaping(false).loader(new DelegatingLoader(ImmutableList.of(new StringLoader()))).build();
    }

    @Override
    public Long savePartner(SavePartnerCommand command) {
        log.info("Saving affiliate partner: [{}]", command);
        Validate.notBlank(command.getName(), "Affiliate partner name required");
        AffiliatePartnerEntity entity = partnerRepository.getOptional(Entities.partner.name.equalsIgnoreCase(command.getName())).orElse(new AffiliatePartnerEntity());
        entity.setName(command.getName());
        entity.setActive(command.isActive());
        entity.setLeadReportUrl(command.getLeadReportUrl());
        entity.setRepeatedClientLeadReportUrl(command.getRepeatedClientLeadReportUrl());
        entity.setActionReportUrl(command.getActionReportUrl());
        entity.setRepeatedClientActionReportUrl(command.getRepeatedClientActionReportUrl());
        entity.setLeadConditionWorkflowActivityName(command.getLeadConditionWorkflowActivityName());
        entity.setLeadConditionWorkflowActivityResolution(command.getLeadConditionWorkflowActivityResolution());
        entity.setApiKey(command.getApiKey());
        Long id = partnerRepository.saveAndFlush(entity).getId();

        if (!StringUtils.isBlank(command.getApiKey())) {
            long apiKeyCount = partnerRepository.count(Entities.partner.apiKey.eq(command.getApiKey()));
            Validate.isTrue(apiKeyCount == 1, "Duplicate API key");
        }
        return id;
    }

    @Override
    public Optional<Long> addLead(AddLeadCommand command) {
        log.info("Adding lead: [{}]", command);
        Validate.notNull(command.getClientId(), "Null client id");
        Validate.notNull(command.getApplicationId(), "Null application id");
        Optional<AffiliateLeadEntity> existing = findLeadByClientIdAndApplicationId(command.getClientId(), command.getApplicationId());
        if (existing.isPresent()) {
            log.warn("Ignoring new lead, client already has one: [{}]", existing.get());
            return Optional.empty();
        }

        Optional<AffiliatePartnerEntity> partner = partnerRepository.getOptional(Entities.partner.name.equalsIgnoreCase(command.getAffiliateName()));

        AffiliateLeadEntity entity = new AffiliateLeadEntity();
        entity.setUnknownPartner(!partner.isPresent());
        partner.ifPresent(entity::setPartner);
        entity.setClientId(command.getClientId());
        entity.setApplicationId(command.getApplicationId());
        entity.setAffiliateName(command.getAffiliateName());
        entity.setCampaign(command.getCampaign());
        entity.setAffiliateLeadId(command.getAffiliateLeadId());
        entity.setSubAffiliateLeadId1(command.getSubAffiliateLeadId1());
        entity.setSubAffiliateLeadId2(command.getSubAffiliateLeadId2());
        entity.setSubAffiliateLeadId3(command.getSubAffiliateLeadId3());
        entity.setRepeatedClient(command.isRepeatedClient());

        Long id = leadRepository.saveAndFlush(entity).getId();
        return Optional.of(id);
    }

    @Override
    public Optional<Long> reportEvent(ReportEventCommand command) {
        log.info("Reporting event: [{}]", command);
        Validate.notNull(command.getClientId(), "Null client id");
        Validate.notNull(command.getApplicationId(), "Null application id");
        Validate.notNull(command.getEventType(), "Null event type");

        Optional<AffiliateLeadEntity> leadMaybe = findLeadByClientIdAndApplicationId(command.getClientId(), command.getApplicationId());
        if (!leadMaybe.isPresent()) {
            log.warn("Ignoring event, leadMaybe not found for event: [{}]", command);
            return Optional.empty();
        }

        Optional<AffiliateEventEntity> existingEvent = eventRepository.getOptional(
            Entities.event.lead.eq(leadMaybe.get())
                .and(Entities.event.applicationId.eq(command.getApplicationId()))
                .and(Entities.event.eventType.eq(command.getEventType())));
        if (existingEvent.isPresent()) {
            log.warn("Ignoring event, found existing with same client id and type: [{}]", existingEvent.get());
            return Optional.empty();
        }

        AffiliateLeadEntity lead = leadMaybe.get();
        AffiliateEventEntity entity = new AffiliateEventEntity();
        Optional<String> reportUrl = buildReportUrl(lead, command);
        entity.setPartner(lead.getPartner());
        entity.setLead(lead);
        entity.setEventType(command.getEventType());
        entity.setReportStatus(reportUrl.isPresent() ? ReportStatus.PENDING : ReportStatus.IGNORED);
        entity.setClientId(lead.getClientId());
        entity.setApplicationId(command.getApplicationId());
        entity.setLoanId(command.getLoanId());
        entity.setReportRetryAttempts(0);
        entity.setNextReportAttemptAt(TimeMachine.now());
        entity.setReportUrl(reportUrl.orElse(""));
        Long id = eventRepository.saveAndFlush(entity).getId();
        return Optional.of(id);
    }

    private Optional<AffiliateLeadEntity> findLead(Long clientId) {
        List<AffiliateLeadEntity> leads = leadRepository.findAll(Entities.lead.clientId.eq(clientId), Entities.lead.id.desc());
        if (leads.isEmpty()) {
            return Optional.empty();
        } else {
            // take latest lead
            // should be only one, but in Alfa migrated data there are multiple leads per client
            return Optional.of(leads.get(0));
        }
    }

    private Optional<AffiliateLeadEntity> findLeadByClientIdAndApplicationId(Long clientId, Long applicationId) {
        return leadRepository.findFirst(Entities.lead.clientId.eq(clientId).and(Entities.lead.applicationId.eq(applicationId)), Entities.lead.id.desc());
    }

    @SneakyThrows
    private Optional<String> buildReportUrl(AffiliateLeadEntity lead, ReportEventCommand command) {
        AffiliatePartnerEntity partner = lead.getPartner();
        if (lead.isUnknownPartner() || partner == null || !partner.isActive()) {
            return Optional.empty();
        }

        String urlTemplate = getReportUrlTemplate(lead, command, partner);
        if (StringUtils.isBlank(urlTemplate)) {
            return Optional.empty();
        }

        AffiliateLead leadValueObject = lead.toValueObject();

        Map<String, Object> context = new HashMap<>();
        context.put("lead", leadValueObject);
        affiliateRegistry.getContextProvider().ifPresent(provider -> context.putAll(provider.getContext(leadValueObject)));
        // legacy support
        context.put("AFFILIATE_NAME", partner.getName());
        context.put("CAMPAIGN", lead.getCampaign());
        context.put("AFFILIATE_LEAD_ID", lead.getAffiliateLeadId());
        context.put("SUB_AFFILIATE_LEAD_ID_1", lead.getSubAffiliateLeadId1());
        context.put("SUB_AFFILIATE_LEAD_ID_2", lead.getSubAffiliateLeadId2());
        context.put("SUB_AFFILIATE_LEAD_ID_3", lead.getSubAffiliateLeadId3());

        StringWriter sw = new StringWriter();
        pebbleEngine.getTemplate(urlTemplate).evaluate(sw, context);
        String url = sw.toString();
        return Optional.of(url);
    }

    private String getReportUrlTemplate(AffiliateLeadEntity lead, ReportEventCommand command, AffiliatePartnerEntity partner) {
        if (lead.isRepeatedClient()) {
            return getRepeatedClientReportUrl(command, partner);
        } else {
            return getNewClientReportUrl(command, partner);
        }
    }

    private String getRepeatedClientReportUrl(ReportEventCommand command, AffiliatePartnerEntity partner) {
        if (command.getEventType() == EventType.LEAD) {
            return partner.getRepeatedClientLeadReportUrl();
        } else if (command.getEventType() == EventType.ACTION) {
            return partner.getRepeatedClientActionReportUrl();
        }
        return null;
    }

    private String getNewClientReportUrl(ReportEventCommand command, AffiliatePartnerEntity partner) {
        if (command.getEventType() == EventType.LEAD) {
            return partner.getLeadReportUrl();
        } else if (command.getEventType() == EventType.ACTION) {
            return partner.getActionReportUrl();
        }
        return null;
    }

    @Override
    public Optional<LeadReport> findLeadReportByClientId(Long clientId) {
        return findLead(clientId).map(this::mapLead);
    }

    @Override
    public Optional<LeadReport> findLeadReportByClientIdAndApplicationId(Long clientId, Long applicationId) {
        return Optional.ofNullable(
            leadRepository.findOneOrNull(Entities.lead.clientId.eq(clientId))
        ).map(this::mapLead);
    }

    @Override
    public Optional<AffiliatePartner> findActivePartnerByApiKey(String apiKey) {
        Validate.notBlank(apiKey, "Blank api key");
        AffiliatePartnerEntity entity = partnerRepository.findOne(Entities.partner.apiKey.eq(apiKey).and(Entities.partner.active.isTrue()));
        return Optional.ofNullable(entity).map(AffiliatePartnerEntity::toValueObject);
    }

    @Override
    public Long saveAffiliateRequest(SaveAffiliateRequestCommand command) {
        log.info("Saving affiliate request: [{}]", command);
        AffiliateRequestEntity entity = new AffiliateRequestEntity();
        entity.setRequestType(command.getRequestType());
        entity.setApplicationId(command.getApplicationId());
        entity.setClientId(command.getClientId());

        Optional.ofNullable(command.getRequest()).map(JsonUtils::toJsonNode).ifPresent(entity::setRequest);
        Optional.ofNullable(command.getResponse()).map(JsonUtils::toJsonNode).ifPresent(entity::setResponse);

        return requestRepository.saveAndFlush(entity).getId();
    }

    private LeadReport mapLead(AffiliateLeadEntity lead) {
        LeadReport report = new LeadReport();
        report.setClientId(lead.getClientId());
        report.setLeadId(lead.getId());
        if (lead.getPartner() != null) {
            report.setPartnerId(lead.getPartner().getId());
            report.setLeadConditionWorkflowActivityName(lead.getPartner().getLeadConditionWorkflowActivityName());
            report.setLeadConditionWorkflowActivityResolution(lead.getPartner().getLeadConditionWorkflowActivityResolution());
        }
        report.setUnknownPartner(lead.isUnknownPartner());
        report.setRepeatedClient(lead.isRepeatedClient());
        List<EventType> eventTypes = queryFactory.selectDistinct(Entities.event.eventType).from(Entities.event)
            .where(Entities.event.lead.eq(lead)).fetch();
        report.setReportedEventTypes(eventTypes);
        return report;
    }
}
