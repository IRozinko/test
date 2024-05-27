package fintech.bo.spain.asnef.api.model;

import lombok.Data;

@Data
public class ImportAsnefFileRequest {

    private String type;

    private Long fileId;
}
