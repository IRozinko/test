/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.affiliate.tables.records;


import fintech.bo.db.jooq.affiliate.tables.Partner;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
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
public class PartnerRecord extends UpdatableRecordImpl<PartnerRecord> implements Record15<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Boolean, String, String, String, String, String, String, String> {

    private static final long serialVersionUID = -1902416664;

    /**
     * Setter for <code>affiliate.partner.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>affiliate.partner.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>affiliate.partner.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>affiliate.partner.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>affiliate.partner.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>affiliate.partner.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>affiliate.partner.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>affiliate.partner.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>affiliate.partner.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>affiliate.partner.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>affiliate.partner.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>affiliate.partner.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>affiliate.partner.action_report_url</code>.
     */
    public void setActionReportUrl(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>affiliate.partner.action_report_url</code>.
     */
    public String getActionReportUrl() {
        return (String) get(6);
    }

    /**
     * Setter for <code>affiliate.partner.active</code>.
     */
    public void setActive(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>affiliate.partner.active</code>.
     */
    public Boolean getActive() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>affiliate.partner.lead_condition_workflow_activity_name</code>.
     */
    public void setLeadConditionWorkflowActivityName(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>affiliate.partner.lead_condition_workflow_activity_name</code>.
     */
    public String getLeadConditionWorkflowActivityName() {
        return (String) get(8);
    }

    /**
     * Setter for <code>affiliate.partner.lead_condition_workflow_activity_resolution</code>.
     */
    public void setLeadConditionWorkflowActivityResolution(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>affiliate.partner.lead_condition_workflow_activity_resolution</code>.
     */
    public String getLeadConditionWorkflowActivityResolution() {
        return (String) get(9);
    }

    /**
     * Setter for <code>affiliate.partner.lead_report_url</code>.
     */
    public void setLeadReportUrl(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>affiliate.partner.lead_report_url</code>.
     */
    public String getLeadReportUrl() {
        return (String) get(10);
    }

    /**
     * Setter for <code>affiliate.partner.name</code>.
     */
    public void setName(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>affiliate.partner.name</code>.
     */
    public String getName() {
        return (String) get(11);
    }

    /**
     * Setter for <code>affiliate.partner.api_key</code>.
     */
    public void setApiKey(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>affiliate.partner.api_key</code>.
     */
    public String getApiKey() {
        return (String) get(12);
    }

    /**
     * Setter for <code>affiliate.partner.repeated_client_action_report_url</code>.
     */
    public void setRepeatedClientActionReportUrl(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>affiliate.partner.repeated_client_action_report_url</code>.
     */
    public String getRepeatedClientActionReportUrl() {
        return (String) get(13);
    }

    /**
     * Setter for <code>affiliate.partner.repeated_client_lead_report_url</code>.
     */
    public void setRepeatedClientLeadReportUrl(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>affiliate.partner.repeated_client_lead_report_url</code>.
     */
    public String getRepeatedClientLeadReportUrl() {
        return (String) get(14);
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
    // Record15 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Boolean, String, String, String, String, String, String, String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Boolean, String, String, String, String, String, String, String> valuesRow() {
        return (Row15) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Partner.PARTNER.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Partner.PARTNER.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Partner.PARTNER.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Partner.PARTNER.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Partner.PARTNER.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Partner.PARTNER.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Partner.PARTNER.ACTION_REPORT_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field8() {
        return Partner.PARTNER.ACTIVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Partner.PARTNER.LEAD_CONDITION_WORKFLOW_ACTIVITY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Partner.PARTNER.LEAD_CONDITION_WORKFLOW_ACTIVITY_RESOLUTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Partner.PARTNER.LEAD_REPORT_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Partner.PARTNER.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Partner.PARTNER.API_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return Partner.PARTNER.REPEATED_CLIENT_ACTION_REPORT_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Partner.PARTNER.REPEATED_CLIENT_LEAD_REPORT_URL;
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
        return getActionReportUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value8() {
        return getActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getLeadConditionWorkflowActivityName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getLeadConditionWorkflowActivityResolution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getLeadReportUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getApiKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getRepeatedClientActionReportUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getRepeatedClientLeadReportUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value7(String value) {
        setActionReportUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value8(Boolean value) {
        setActive(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value9(String value) {
        setLeadConditionWorkflowActivityName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value10(String value) {
        setLeadConditionWorkflowActivityResolution(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value11(String value) {
        setLeadReportUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value12(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value13(String value) {
        setApiKey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value14(String value) {
        setRepeatedClientActionReportUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord value15(String value) {
        setRepeatedClientLeadReportUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, String value7, Boolean value8, String value9, String value10, String value11, String value12, String value13, String value14, String value15) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PartnerRecord
     */
    public PartnerRecord() {
        super(Partner.PARTNER);
    }

    /**
     * Create a detached, initialised PartnerRecord
     */
    public PartnerRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, String actionReportUrl, Boolean active, String leadConditionWorkflowActivityName, String leadConditionWorkflowActivityResolution, String leadReportUrl, String name, String apiKey, String repeatedClientActionReportUrl, String repeatedClientLeadReportUrl) {
        super(Partner.PARTNER);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, actionReportUrl);
        set(7, active);
        set(8, leadConditionWorkflowActivityName);
        set(9, leadConditionWorkflowActivityResolution);
        set(10, leadReportUrl);
        set(11, name);
        set(12, apiKey);
        set(13, repeatedClientActionReportUrl);
        set(14, repeatedClientLeadReportUrl);
    }
}
