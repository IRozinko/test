package fintech.dowjones.db;

import fintech.db.BaseEntity;
import fintech.dowjones.MatchResult;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "match", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "search_result_id", name = "idx_match_search_result_id"),
})
public class MatchEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "search_result_id")
    private SearchResultEntity searchResult;

    private BigDecimal score;
    private String riskIndicator;
    private String gender;
    private String primaryName;
    private String countryCode;
    private int dateOfBirthYear;
    private int dateOfBirthMonth;
    private int dateOfBirthDay;

    private String firstName;
    private String lastName;
    private String secondLastName;
    private String secondFirstName;
    private String maidenName;

    public MatchResult toValueObject() {
        MatchResult matchResult = new MatchResult();
        matchResult.setId(this.id);
        matchResult.setSearchResultId(searchResult.getId());
        matchResult.setScore(this.score);
        matchResult.setRiskIndicator(this.riskIndicator);
        matchResult.setPrimaryName(this.primaryName);
        matchResult.setGender(this.gender);
        matchResult.setCountryCode(this.countryCode);
        matchResult.setDateOfBirthYear(this.dateOfBirthYear);
        matchResult.setDateOfBirthMonth(this.dateOfBirthMonth);
        matchResult.setDateOfBirthDay(this.dateOfBirthDay);
        matchResult.setFirstName(this.firstName);
        matchResult.setLastName(this.lastName);
        matchResult.setSecondLastName(this.secondLastName);
        matchResult.setSecondFirstName(this.secondFirstName);
        matchResult.setMaidenName(this.maidenName);
        return matchResult;
    }
}
