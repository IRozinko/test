package fintech.dowjones;

import java.util.Optional;

public interface DowJonesService {

    DowJonesRequest search(DowJonesRequestData data);

    DowJonesRequest getDowJonesRequest(Long requestId);

    Optional<SearchResult> getSearchResult(Long requestId);

}
