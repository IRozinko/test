package fintech.spain.alfa.web.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplicationInfo {

    private Long id;
    private String status;
    private String statusDetail;
    private String type;
    private LocalDate closeDate;
    private LocalDateTime submittedAt;
    private BigDecimal requestedPrincipal;
    private BigDecimal offeredPrincipal;
    private String currentActivity;
}
