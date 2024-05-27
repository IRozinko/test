package fintech.affiliate.db;

public class Entities {

    public static final String SCHEMA = "affiliate";

    public static final QAffiliatePartnerEntity partner = QAffiliatePartnerEntity.affiliatePartnerEntity;
    public static final QAffiliateLeadEntity lead = QAffiliateLeadEntity.affiliateLeadEntity;
    public static final QAffiliateEventEntity event = QAffiliateEventEntity.affiliateEventEntity;
    public static final QAffiliateRequestEntity request = QAffiliateRequestEntity.affiliateRequestEntity;
}
