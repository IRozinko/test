package fintech.bo.spain.asnef.model;

import lombok.Data;

@Data
public class ImportAsnefFileRequest {

    private String type;

    private Long fileId;
}
