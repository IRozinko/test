package fintech.dowjones;

import fintech.dowjones.db.MatchEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SearchResult {
    private Long id;
    private Long requestId;
    private int totalHits;
    private int hitsFrom;
    private int hitsTo;
    private boolean truncated;
    private String cachedResultsId;
    private List<MatchEntity> matchList;
}
