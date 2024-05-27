package fintech.spain.experian.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaisListOperacionesResponse {

    private Long id;
    private Long clientId;
    private Long applicationId;
    private String documentNumber;
    private ExperianStatus status;
    private String error;
    private String requestBody;
    private String responseBody;

    private Integer numeroRegistrosDevueltos;
    protected LocalDateTime createdAt;
}
