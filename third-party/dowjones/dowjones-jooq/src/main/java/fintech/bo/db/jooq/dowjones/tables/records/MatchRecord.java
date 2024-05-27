/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.dowjones.tables.records;


import fintech.bo.db.jooq.dowjones.tables.Match;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
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
public class MatchRecord extends UpdatableRecordImpl<MatchRecord> implements Record20<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, BigDecimal, String, String, String, String, Integer, Integer, Integer, String, String, String, String, String> {

    private static final long serialVersionUID = -161931705;

    /**
     * Setter for <code>dowjones.match.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>dowjones.match.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>dowjones.match.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>dowjones.match.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>dowjones.match.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>dowjones.match.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>dowjones.match.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>dowjones.match.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>dowjones.match.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>dowjones.match.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>dowjones.match.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>dowjones.match.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>dowjones.match.search_result_id</code>.
     */
    public void setSearchResultId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>dowjones.match.search_result_id</code>.
     */
    public Long getSearchResultId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>dowjones.match.score</code>.
     */
    public void setScore(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>dowjones.match.score</code>.
     */
    public BigDecimal getScore() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>dowjones.match.risk_indicator</code>.
     */
    public void setRiskIndicator(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>dowjones.match.risk_indicator</code>.
     */
    public String getRiskIndicator() {
        return (String) get(8);
    }

    /**
     * Setter for <code>dowjones.match.primary_name</code>.
     */
    public void setPrimaryName(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>dowjones.match.primary_name</code>.
     */
    public String getPrimaryName() {
        return (String) get(9);
    }

    /**
     * Setter for <code>dowjones.match.country_code</code>.
     */
    public void setCountryCode(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>dowjones.match.country_code</code>.
     */
    public String getCountryCode() {
        return (String) get(10);
    }

    /**
     * Setter for <code>dowjones.match.gender</code>.
     */
    public void setGender(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>dowjones.match.gender</code>.
     */
    public String getGender() {
        return (String) get(11);
    }

    /**
     * Setter for <code>dowjones.match.date_of_birth_year</code>.
     */
    public void setDateOfBirthYear(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>dowjones.match.date_of_birth_year</code>.
     */
    public Integer getDateOfBirthYear() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>dowjones.match.date_of_birth_month</code>.
     */
    public void setDateOfBirthMonth(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>dowjones.match.date_of_birth_month</code>.
     */
    public Integer getDateOfBirthMonth() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>dowjones.match.date_of_birth_day</code>.
     */
    public void setDateOfBirthDay(Integer value) {
        set(14, value);
    }

    /**
     * Getter for <code>dowjones.match.date_of_birth_day</code>.
     */
    public Integer getDateOfBirthDay() {
        return (Integer) get(14);
    }

    /**
     * Setter for <code>dowjones.match.first_name</code>.
     */
    public void setFirstName(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>dowjones.match.first_name</code>.
     */
    public String getFirstName() {
        return (String) get(15);
    }

    /**
     * Setter for <code>dowjones.match.last_name</code>.
     */
    public void setLastName(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>dowjones.match.last_name</code>.
     */
    public String getLastName() {
        return (String) get(16);
    }

    /**
     * Setter for <code>dowjones.match.second_last_name</code>.
     */
    public void setSecondLastName(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>dowjones.match.second_last_name</code>.
     */
    public String getSecondLastName() {
        return (String) get(17);
    }

    /**
     * Setter for <code>dowjones.match.second_first_name</code>.
     */
    public void setSecondFirstName(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>dowjones.match.second_first_name</code>.
     */
    public String getSecondFirstName() {
        return (String) get(18);
    }

    /**
     * Setter for <code>dowjones.match.maiden_name</code>.
     */
    public void setMaidenName(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>dowjones.match.maiden_name</code>.
     */
    public String getMaidenName() {
        return (String) get(19);
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
    // Record20 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row20<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, BigDecimal, String, String, String, String, Integer, Integer, Integer, String, String, String, String, String> fieldsRow() {
        return (Row20) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row20<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, BigDecimal, String, String, String, String, Integer, Integer, Integer, String, String, String, String, String> valuesRow() {
        return (Row20) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Match.MATCH.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Match.MATCH.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Match.MATCH.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Match.MATCH.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Match.MATCH.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Match.MATCH.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return Match.MATCH.SEARCH_RESULT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field8() {
        return Match.MATCH.SCORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Match.MATCH.RISK_INDICATOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Match.MATCH.PRIMARY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Match.MATCH.COUNTRY_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Match.MATCH.GENDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field13() {
        return Match.MATCH.DATE_OF_BIRTH_YEAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return Match.MATCH.DATE_OF_BIRTH_MONTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field15() {
        return Match.MATCH.DATE_OF_BIRTH_DAY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Match.MATCH.FIRST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return Match.MATCH.LAST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field18() {
        return Match.MATCH.SECOND_LAST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field19() {
        return Match.MATCH.SECOND_FIRST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field20() {
        return Match.MATCH.MAIDEN_NAME;
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
    public Long value7() {
        return getSearchResultId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value8() {
        return getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getRiskIndicator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getPrimaryName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getCountryCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getGender();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value13() {
        return getDateOfBirthYear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getDateOfBirthMonth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value15() {
        return getDateOfBirthDay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getFirstName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getLastName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value18() {
        return getSecondLastName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value19() {
        return getSecondFirstName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value20() {
        return getMaidenName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value7(Long value) {
        setSearchResultId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value8(BigDecimal value) {
        setScore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value9(String value) {
        setRiskIndicator(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value10(String value) {
        setPrimaryName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value11(String value) {
        setCountryCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value12(String value) {
        setGender(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value13(Integer value) {
        setDateOfBirthYear(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value14(Integer value) {
        setDateOfBirthMonth(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value15(Integer value) {
        setDateOfBirthDay(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value16(String value) {
        setFirstName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value17(String value) {
        setLastName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value18(String value) {
        setSecondLastName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value19(String value) {
        setSecondFirstName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord value20(String value) {
        setMaidenName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, BigDecimal value8, String value9, String value10, String value11, String value12, Integer value13, Integer value14, Integer value15, String value16, String value17, String value18, String value19, String value20) {
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
        value18(value18);
        value19(value19);
        value20(value20);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MatchRecord
     */
    public MatchRecord() {
        super(Match.MATCH);
    }

    /**
     * Create a detached, initialised MatchRecord
     */
    public MatchRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long searchResultId, BigDecimal score, String riskIndicator, String primaryName, String countryCode, String gender, Integer dateOfBirthYear, Integer dateOfBirthMonth, Integer dateOfBirthDay, String firstName, String lastName, String secondLastName, String secondFirstName, String maidenName) {
        super(Match.MATCH);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, searchResultId);
        set(7, score);
        set(8, riskIndicator);
        set(9, primaryName);
        set(10, countryCode);
        set(11, gender);
        set(12, dateOfBirthYear);
        set(13, dateOfBirthMonth);
        set(14, dateOfBirthDay);
        set(15, firstName);
        set(16, lastName);
        set(17, secondLastName);
        set(18, secondFirstName);
        set(19, maidenName);
    }
}
