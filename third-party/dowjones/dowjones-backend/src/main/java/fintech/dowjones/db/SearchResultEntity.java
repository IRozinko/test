package fintech.dowjones.db;

import com.google.common.collect.Lists;
import fintech.db.BaseEntity;
import fintech.dowjones.SearchResult;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "search_result", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "requestId", name = "idx_search_result_request_id"),
})
public class SearchResultEntity extends BaseEntity {

    @Column(nullable = false)
    private Long requestId;

    private int totalHits;
    private int hitsFrom;
    private int hitsTo;
    private boolean truncated;
    private String cachedResultsId;

    @OneToMany(mappedBy = "searchResult", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MatchEntity> matches = Lists.newArrayList();

    public SearchResult toValueObject() {
        SearchResult searchResult = new SearchResult();
        searchResult.setId(this.id);
        searchResult.setRequestId(this.requestId);
        searchResult.setTotalHits(this.totalHits);
        searchResult.setHitsFrom(this.hitsFrom);
        searchResult.setHitsTo(this.hitsTo);
        searchResult.setTruncated(this.truncated);
        searchResult.setCachedResultsId(this.cachedResultsId);
        searchResult.setMatchList(this.matches);
        return searchResult;
    }
}
