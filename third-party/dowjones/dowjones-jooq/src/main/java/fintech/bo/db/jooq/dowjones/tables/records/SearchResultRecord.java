/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.dowjones.tables.records;


import fintech.bo.db.jooq.dowjones.tables.SearchResult;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
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
public class SearchResultRecord extends UpdatableRecordImpl<SearchResultRecord> implements Record12<Long, LocalDateTime, String, Long, LocalDateTime, String, Integer, Integer, Integer, Boolean, String, Long> {

    private static final long serialVersionUID = -750719999;

    /**
     * Setter for <code>dowjones.search_result.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>dowjones.search_result.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>dowjones.search_result.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>dowjones.search_result.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>dowjones.search_result.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>dowjones.search_result.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>dowjones.search_result.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>dowjones.search_result.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>dowjones.search_result.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>dowjones.search_result.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>dowjones.search_result.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>dowjones.search_result.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>dowjones.search_result.total_hits</code>.
     */
    public void setTotalHits(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>dowjones.search_result.total_hits</code>.
     */
    public Integer getTotalHits() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>dowjones.search_result.hits_from</code>.
     */
    public void setHitsFrom(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>dowjones.search_result.hits_from</code>.
     */
    public Integer getHitsFrom() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>dowjones.search_result.hits_to</code>.
     */
    public void setHitsTo(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>dowjones.search_result.hits_to</code>.
     */
    public Integer getHitsTo() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>dowjones.search_result.truncated</code>.
     */
    public void setTruncated(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>dowjones.search_result.truncated</code>.
     */
    public Boolean getTruncated() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>dowjones.search_result.cached_results_id</code>.
     */
    public void setCachedResultsId(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>dowjones.search_result.cached_results_id</code>.
     */
    public String getCachedResultsId() {
        return (String) get(10);
    }

    /**
     * Setter for <code>dowjones.search_result.request_id</code>.
     */
    public void setRequestId(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>dowjones.search_result.request_id</code>.
     */
    public Long getRequestId() {
        return (Long) get(11);
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
    // Record12 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, Integer, Integer, Integer, Boolean, String, Long> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, Integer, Integer, Integer, Boolean, String, Long> valuesRow() {
        return (Row12) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return SearchResult.SEARCH_RESULT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return SearchResult.SEARCH_RESULT.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return SearchResult.SEARCH_RESULT.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return SearchResult.SEARCH_RESULT.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return SearchResult.SEARCH_RESULT.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return SearchResult.SEARCH_RESULT.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return SearchResult.SEARCH_RESULT.TOTAL_HITS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return SearchResult.SEARCH_RESULT.HITS_FROM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return SearchResult.SEARCH_RESULT.HITS_TO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field10() {
        return SearchResult.SEARCH_RESULT.TRUNCATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return SearchResult.SEARCH_RESULT.CACHED_RESULTS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field12() {
        return SearchResult.SEARCH_RESULT.REQUEST_ID;
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
    public Integer value7() {
        return getTotalHits();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getHitsFrom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getHitsTo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value10() {
        return getTruncated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getCachedResultsId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value12() {
        return getRequestId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value7(Integer value) {
        setTotalHits(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value8(Integer value) {
        setHitsFrom(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value9(Integer value) {
        setHitsTo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value10(Boolean value) {
        setTruncated(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value11(String value) {
        setCachedResultsId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord value12(Long value) {
        setRequestId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Integer value7, Integer value8, Integer value9, Boolean value10, String value11, Long value12) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SearchResultRecord
     */
    public SearchResultRecord() {
        super(SearchResult.SEARCH_RESULT);
    }

    /**
     * Create a detached, initialised SearchResultRecord
     */
    public SearchResultRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Integer totalHits, Integer hitsFrom, Integer hitsTo, Boolean truncated, String cachedResultsId, Long requestId) {
        super(SearchResult.SEARCH_RESULT);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, totalHits);
        set(7, hitsFrom);
        set(8, hitsTo);
        set(9, truncated);
        set(10, cachedResultsId);
        set(11, requestId);
    }
}
