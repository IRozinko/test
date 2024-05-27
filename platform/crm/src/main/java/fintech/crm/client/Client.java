package fintech.crm.client;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.MoreObjects.firstNonNull;

@Data
@ToString(exclude = {"phone", "accountNumber", "documentNumber"})
public class Client {

    private Long id;
    private String number;
    private String phone;
    private String email;
    private String firstName;
    private String secondFirstName;
    private String lastName;
    private String secondLastName;
    private String maidenName;
    private String documentNumber;
    private String accountNumber;
    private String title;
    private Gender gender;
    private LocalDate dateOfBirth;
    private boolean acceptTerms;
    private boolean acceptMarketing;
    private boolean acceptVerification;
    private boolean acceptPrivacyPolicy;
    private boolean blockCommunication;
    private boolean excludedFromASNEF;
    private boolean transferredToLoc;
    private Map<String, String> attributes = new HashMap<>();
    private LocalDateTime createdAt;
    private Set<ClientSegment> segments = new HashSet<>();
    private boolean deleted;
    private String locale;

    public boolean isInSegment(String segment) {
        return segments.stream().map(ClientSegment::getSegment).anyMatch(segment::equals);
    }

    public String getFirstAndLastName() {
        return firstNonNull(firstName, "") + " " + firstNonNull(lastName, "");
    }

    public String getFullName() {
        return String.join(" ", firstNonNull(firstName, ""), firstNonNull(lastName, ""), firstNonNull(secondLastName, "")).trim();
    }
}
