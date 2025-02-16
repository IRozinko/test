/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.affiliate.tables.records;


import fintech.bo.db.jooq.affiliate.tables.Lead;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LeadRecord extends UpdatableRecordImpl<LeadRecord> implements Record17<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, Long, String, Long, String, String, String, Boolean, Long, Boolean> {

    private static final long serialVersionUID = -1890205367;

    /**
     * Setter for <code>affiliate.lead.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>affiliate.lead.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>affiliate.lead.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>affiliate.lead.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>affiliate.lead.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>affiliate.lead.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>affiliate.lead.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>affiliate.lead.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>affiliate.lead.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>affiliate.lead.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>affiliate.lead.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>affiliate.lead.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>affiliate.lead.affiliate_lead_id</code>.
     */
    public void setAffiliateLeadId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>affiliate.lead.affiliate_lead_id</code>.
     */
    public String getAffiliateLeadId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>affiliate.lead.affiliate_name</code>.
     */
    public void setAffiliateName(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>affiliate.lead.affiliate_name</code>.
     */
    public String getAffiliateName() {
        return (String) get(7);
    }

    /**
     * Setter for <code>affiliate.lead.application_id</code>.
     */
    public void setApplicationId(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>affiliate.lead.application_id</code>.
     */
    public Long getApplicationId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>affiliate.lead.campaign</code>.
     */
    public void setCampaign(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>affiliate.lead.campaign</code>.
     */
    public String getCampaign() {
        return (String) get(9);
    }

    /**
     * Setter for <code>affiliate.lead.client_id</code>.
     */
    public void setClientId(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>affiliate.lead.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>affiliate.lead.sub_affiliate_lead_id1</code>.
     */
    public void setSubAffiliateLeadId1(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>affiliate.lead.sub_affiliate_lead_id1</code>.
     */
    public String getSubAffiliateLeadId1() {
        return (String) get(11);
    }

    /**
     * Setter for <code>affiliate.lead.sub_affiliate_lead_id2</code>.
     */
    public void setSubAffiliateLeadId2(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>affiliate.lead.sub_affiliate_lead_id2</code>.
     */
    public String getSubAffiliateLeadId2() {
        return (String) get(12);
    }

    /**
     * Setter for <code>affiliate.lead.sub_affiliate_lead_id3</code>.
     */
    public void setSubAffiliateLeadId3(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>affiliate.lead.sub_affiliate_lead_id3</code>.
     */
    public String getSubAffiliateLeadId3() {
        return (String) get(13);
    }

    /**
     * Setter for <code>affiliate.lead.unknown_partner</code>.
     */
    public void setUnknownPartner(Boolean value) {
        set(14, value);
    }

    /**
     * Getter for <code>affiliate.lead.unknown_partner</code>.
     */
    public Boolean getUnknownPartner() {
        return (Boolean) get(14);
    }

    /**
     * Setter for <code>affiliate.lead.partner_id</code>.
     */
    public void setPartnerId(Long value) {
        set(15, value);
    }

    /**
     * Getter for <code>affiliate.lead.partner_id</code>.
     */
    public Long getPartnerId() {
        return (Long) get(15);
    }

    /**
     * Setter for <code>affiliate.lead.repeated_client</code>.
     */
    public void setRepeatedClient(Boolean value) {
        set(16, value);
    }

    /**
     * Getter for <code>affiliate.lead.repeated_client</code>.
     */
    public Boolean getRepeatedClient() {
        return (Boolean) get(16);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record17 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, Long, String, Long, String, String, String, Boolean, Long, Boolean> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, Long, String, Long, String, String, String, Boolean, Long, Boolean> valuesRow() {
        return (Row17) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Lead.LEAD.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Lead.LEAD.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Lead.LEAD.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Lead.LEAD.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Lead.LEAD.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Lead.LEAD.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Lead.LEAD.AFFILIATE_LEAD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Lead.LEAD.AFFILIATE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field9() {
        return Lead.LEAD.APPLICATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Lead.LEAD.CAMPAIGN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field11() {
        return Lead.LEAD.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Lead.LEAD.SUB_AFFILIATE_LEAD_ID1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Lead.LEAD.SUB_AFFILIATE_LEAD_ID2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return Lead.LEAD.SUB_AFFILIATE_LEAD_ID3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field15() {
        return Lead.LEAD.UNKNOWN_PARTNER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field16() {
        return Lead.LEAD.PARTNER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field17() {
        return Lead.LEAD.REPEATED_CLIENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value2() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value4() {
        return getEntityVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value5() {
        return getUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getAffiliateLeadId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getAffiliateName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value9() {
        return getApplicationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getCampaign();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value11() {
        return getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getSubAffiliateLeadId1();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getSubAffiliateLeadId2();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getSubAffiliateLeadId3();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value15() {
        return getUnknownPartner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value16() {
        return getPartnerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value17() {
        return getRepeatedClient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value7(String value) {
        setAffiliateLeadId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value8(String value) {
        setAffiliateName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value9(Long value) {
        setApplicationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value10(String value) {
        setCampaign(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value11(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value12(String value) {
        setSubAffiliateLeadId1(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value13(String value) {
        setSubAffiliateLeadId2(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value14(String value) {
        setSubAffiliateLeadId3(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value15(Boolean value) {
        setUnknownPartner(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value16(Long value) {
        setPartnerId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord value17(Boolean value) {
        setRepeatedClient(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LeadRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, String value7, String value8, Long value9, String value10, Long value11, String value12, String value13, String value14, Boolean value15, Long value16, Boolean value17) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LeadRecord
     */
    public LeadRecord() {
        super(Lead.LEAD);
    }

    /**
     * Create a detached, initialised LeadRecord
     */
    public LeadRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, String affiliateLeadId, String affiliateName, Long applicationId, String campaign, Long clientId, String subAffiliateLeadId1, String subAffiliateLeadId2, String subAffiliateLeadId3, Boolean unknownPartner, Long partnerId, Boolean repeatedClient) {
        super(Lead.LEAD);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, affiliateLeadId);
        set(7, affiliateName);
        set(8, applicationId);
        set(9, campaign);
        set(10, clientId);
        set(11, subAffiliateLeadId1);
        set(12, subAffiliateLeadId2);
        set(13, subAffiliateLeadId3);
        set(14, unknownPartner);
        set(15, partnerId);
        set(16, repeatedClient);
    }
}
