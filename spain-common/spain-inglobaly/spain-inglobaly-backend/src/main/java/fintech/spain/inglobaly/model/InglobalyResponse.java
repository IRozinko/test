package fintech.spain.inglobaly.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InglobalyResponse {

    private Long id;
    private Long clientId;
    private Long applicationId;
    private String requestedDocumentNumber;
    private InglobalyStatus status;
    private String error;
    private LocalDate dateOfBirth;
    private String responseBody;
    private String firstName;
    private String lastName;
    private String secondLastName;

}
