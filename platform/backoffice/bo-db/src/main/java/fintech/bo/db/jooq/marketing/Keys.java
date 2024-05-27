/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.marketing;


import fintech.bo.db.jooq.marketing.tables.MarketingCampaign;
import fintech.bo.db.jooq.marketing.tables.MarketingCommunication;
import fintech.bo.db.jooq.marketing.tables.MarketingTemplate;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCampaignRecord;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCommunicationRecord;
import fintech.bo.db.jooq.marketing.tables.records.MarketingTemplateRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>marketing</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<MarketingCampaignRecord> MARKETING_CAMPAIGN_PKEY = UniqueKeys0.MARKETING_CAMPAIGN_PKEY;
    public static final UniqueKey<MarketingCommunicationRecord> MARKETING_COMMUNICATION_PKEY = UniqueKeys0.MARKETING_COMMUNICATION_PKEY;
    public static final UniqueKey<MarketingTemplateRecord> MARKETING_TEMPLATE_PKEY = UniqueKeys0.MARKETING_TEMPLATE_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<MarketingCampaignRecord, MarketingTemplateRecord> MARKETING_CAMPAIGN__FK_MAIN_MARKETING_TEMPLATE_ID = ForeignKeys0.MARKETING_CAMPAIGN__FK_MAIN_MARKETING_TEMPLATE_ID;
    public static final ForeignKey<MarketingCampaignRecord, MarketingTemplateRecord> MARKETING_CAMPAIGN__FK_REMIND_MARKETING_TEMPLATE_ID = ForeignKeys0.MARKETING_CAMPAIGN__FK_REMIND_MARKETING_TEMPLATE_ID;
    public static final ForeignKey<MarketingCommunicationRecord, MarketingCampaignRecord> MARKETING_COMMUNICATION__FK_MARKETING_CAMPAIGN_ID = ForeignKeys0.MARKETING_COMMUNICATION__FK_MARKETING_CAMPAIGN_ID;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<MarketingCampaignRecord> MARKETING_CAMPAIGN_PKEY = createUniqueKey(MarketingCampaign.MARKETING_CAMPAIGN, "marketing_campaign_pkey", MarketingCampaign.MARKETING_CAMPAIGN.ID);
        public static final UniqueKey<MarketingCommunicationRecord> MARKETING_COMMUNICATION_PKEY = createUniqueKey(MarketingCommunication.MARKETING_COMMUNICATION, "marketing_communication_pkey", MarketingCommunication.MARKETING_COMMUNICATION.ID);
        public static final UniqueKey<MarketingTemplateRecord> MARKETING_TEMPLATE_PKEY = createUniqueKey(MarketingTemplate.MARKETING_TEMPLATE, "marketing_template_pkey", MarketingTemplate.MARKETING_TEMPLATE.ID);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<MarketingCampaignRecord, MarketingTemplateRecord> MARKETING_CAMPAIGN__FK_MAIN_MARKETING_TEMPLATE_ID = createForeignKey(fintech.bo.db.jooq.marketing.Keys.MARKETING_TEMPLATE_PKEY, MarketingCampaign.MARKETING_CAMPAIGN, "marketing_campaign__fk_main_marketing_template_id", MarketingCampaign.MARKETING_CAMPAIGN.MAIN_MARKETING_TEMPLATE_ID);
        public static final ForeignKey<MarketingCampaignRecord, MarketingTemplateRecord> MARKETING_CAMPAIGN__FK_REMIND_MARKETING_TEMPLATE_ID = createForeignKey(fintech.bo.db.jooq.marketing.Keys.MARKETING_TEMPLATE_PKEY, MarketingCampaign.MARKETING_CAMPAIGN, "marketing_campaign__fk_remind_marketing_template_id", MarketingCampaign.MARKETING_CAMPAIGN.REMIND_MARKETING_TEMPLATE_ID);
        public static final ForeignKey<MarketingCommunicationRecord, MarketingCampaignRecord> MARKETING_COMMUNICATION__FK_MARKETING_CAMPAIGN_ID = createForeignKey(fintech.bo.db.jooq.marketing.Keys.MARKETING_CAMPAIGN_PKEY, MarketingCommunication.MARKETING_COMMUNICATION, "marketing_communication__fk_marketing_campaign_id", MarketingCommunication.MARKETING_COMMUNICATION.MARKETING_CAMPAIGN_ID);
    }
}
