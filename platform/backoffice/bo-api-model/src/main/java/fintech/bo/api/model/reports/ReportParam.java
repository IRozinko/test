package fintech.bo.api.model.reports;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReportParam {

    private String id;

    private Long numberValue;

    private String stringValue;

    private LocalDate dateValue;

    private List<String> listValue;

}
