package fintech.bo.api.model.reports;

import lombok.Data;

@Data
public class ReportFileResponse {

    private Long id;

    private Long fileId;

    private String filename;

    private String type;

    private String status;

}
