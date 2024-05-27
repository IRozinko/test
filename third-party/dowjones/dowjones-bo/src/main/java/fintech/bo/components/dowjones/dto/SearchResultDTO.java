package fintech.bo.components.dowjones.dto;

import fintech.bo.components.security.HiddenForSecuredQuery;
import lombok.Data;

@Data
public class SearchResultDTO {

    private Long id;
    private Long clientId;
    private String clientNumber;

    @HiddenForSecuredQuery
    private String email;

    @HiddenForSecuredQuery
    private String phone;

    @HiddenForSecuredQuery
    private String firstName;

    @HiddenForSecuredQuery
    private String secondFirstName;

    @HiddenForSecuredQuery
    private String lastName;

    @HiddenForSecuredQuery
    private String secondLastName;

    @HiddenForSecuredQuery
    private String maidenName;
}
