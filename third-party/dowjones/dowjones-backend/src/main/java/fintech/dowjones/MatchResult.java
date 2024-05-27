package fintech.dowjones;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class MatchResult {

    private Long id;
    private Long searchResultId;
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
}
