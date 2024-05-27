package fintech.bo.spain.asnef.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GenerateAsnefFileRequest {

    private String type;

    private LocalDate batchDate;
    
    private Long limit;
}
