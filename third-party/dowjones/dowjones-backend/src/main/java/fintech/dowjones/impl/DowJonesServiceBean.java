package fintech.dowjones.impl;

import fintech.JsonUtils;
import fintech.dowjones.DowJonesRequest;
import fintech.dowjones.DowJonesRequestData;
import fintech.dowjones.DowJonesResponseData;
import fintech.dowjones.DowJonesService;
import fintech.dowjones.SearchResult;
import fintech.dowjones.db.DowJonesRequestEntity;
import fintech.dowjones.db.DowJonesRequestEntityRepository;
import fintech.dowjones.db.MatchEntity;
import fintech.dowjones.db.SearchResultEntity;
import fintech.dowjones.db.SearchResultEntityRepository;
import fintech.dowjones.model.search.name.DateOfBirth;
import fintech.dowjones.model.search.name.Head;
import fintech.dowjones.model.search.name.Match;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static fintech.dowjones.DowJonesResponseStatus.FAILED;
import static fintech.dowjones.DowJonesResponseStatus.OK;

@Slf4j
@Component
@Transactional
public class DowJonesServiceBean implements DowJonesService {


    @Resource(name = "${dowjones.provider:" + MockDowJonesProviderBean.NAME + "}")
    private DowJonesProvider provider;

    @Autowired
    private DowJonesRequestEntityRepository dowJonesRequestEntityRepository;

    @Autowired
    private SearchResultEntityRepository searchResultEntityRepository;

    @Override
    public DowJonesRequest search(DowJonesRequestData request) {
        DowJonesRequestEntity entity = new DowJonesRequestEntity();
        entity.setClientId(request.getClientId());

        DowJonesResponseData response = provider.search(request);
        log.info("DowJones response: [{}]", response);
        entity.setRequestUrl(response.getUrl());
        entity.setResponseBody(response.getResponseBody());
        entity.setResponseStatusCode(response.getStatusCode());
        entity.setRequestBody(JsonUtils.writeValueAsString(request));
        entity.setError(response.getError());
        entity.setStatus(response.getStatusCode() == HttpStatus.SC_OK ? OK : FAILED);
        DowJonesRequest dowJonesRequest = dowJonesRequestEntityRepository.saveAndFlush(entity).toValueObject();

        if (entity.getStatus() == OK) {
            SearchResultEntity searchResultEntity = new SearchResultEntity();
            Head head = response.getNameSearchResult().getHead();
            searchResultEntity.setRequestId(dowJonesRequest.getId());
            searchResultEntity.setTotalHits(Integer.parseInt(head.getTotalHits()));
            searchResultEntity.setHitsFrom(Integer.parseInt(head.getHitsFrom()));
            searchResultEntity.setHitsTo(Integer.parseInt(head.getHitsTo()));
            searchResultEntity.setTruncated(Boolean.parseBoolean(head.getTruncated()));
            searchResultEntity.setCachedResultsId(head.getCachedResultsId());

            List<MatchEntity> matchEntities = new ArrayList<>();
            List<Match> matches = response.getNameSearchResult().getBody().getMatch();
            for (Match match : matches) {
                MatchEntity matchEntity = new MatchEntity();
                String primaryName = match.getPayload().getPrimaryName();
                matchEntity.setSearchResult(searchResultEntity);
                matchEntity.setScore(parseGentle(match.getScore()));
                matchEntity.setRiskIndicator(match.getPayload().getRiskIcons().getRiskIcon());
                matchEntity.setPrimaryName(primaryName);
                matchEntity.setCountryCode(match.getPayload().getCountryCode());
                matchEntity.setGender(match.getPayload().getGender());
                List<Serializable> dateOfBirthContent = match.getPayload().getDatesOfBirthType().getContent();
                Optional<DateOfBirth> content = parseContent(dateOfBirthContent);
                if (content.isPresent()) {
                    DateOfBirth dateOfBirth = content.get();
                    if (!StringUtils.isBlank(dateOfBirth.getYear())) {
                        matchEntity.setDateOfBirthYear(Integer.parseInt(dateOfBirth.getYear()));
                    }
                    if (!StringUtils.isBlank(dateOfBirth.getMonth())) {
                        matchEntity.setDateOfBirthMonth(Integer.parseInt(dateOfBirth.getMonth()));
                    }
                    if (!StringUtils.isBlank(dateOfBirth.getDay())) {
                        matchEntity.setDateOfBirthDay(Integer.parseInt(dateOfBirth.getDay()));
                    }
                }
                parsePrimaryName(matchEntity, primaryName);
                matchEntities.add(matchEntity);
            }
            searchResultEntity.setMatches(matchEntities);
            searchResultEntityRepository.save(searchResultEntity);
        }
        return dowJonesRequest;
    }

    @Override
    public DowJonesRequest getDowJonesRequest(Long id) {
        return dowJonesRequestEntityRepository.getRequired(id).toValueObject();
    }

    @Override
    public Optional<SearchResult> getSearchResult(Long requestId) {
        return searchResultEntityRepository.findFirstByRequestId(requestId).map(SearchResultEntity::toValueObject);
    }

    private BigDecimal parseGentle(String value) {
        if (value == null) return null;
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Optional<DateOfBirth> parseContent(List<Serializable> content) {
        return content.stream()
            .filter(JAXBElement.class::isInstance)
            .map(c -> (JAXBElement<DateOfBirth>) c)
            .flatMap(o -> o.getValue() != null ? Stream.of(o.getValue()) : Stream.empty())
            .findFirst();

    }


    private void parsePrimaryName(MatchEntity matchEntity, String primaryName) {
        String[] strings = primaryName.split("[\\s,]+");
        String firstName = null;
        String lastName = null;
        String secondLastName = null;
        String secondFirstName = null;
        String maidenName = null;
        switch (strings.length) {
            case 5:
                maidenName = strings[4];
            case 4:
                secondFirstName = strings[3];
            case 3:
                secondLastName = strings[2];
            case 2:
                firstName = strings[1];
            case 1:
                lastName = strings[0];
            default:
                break;
        }
        matchEntity.setFirstName(firstName);
        matchEntity.setLastName(lastName);
        matchEntity.setSecondLastName(secondLastName);
        matchEntity.setSecondFirstName(secondFirstName);
        matchEntity.setMaidenName(maidenName);
    }
}
