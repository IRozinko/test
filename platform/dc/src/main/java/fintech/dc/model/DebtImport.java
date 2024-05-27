package fintech.dc.model;

import lombok.Data;

@Data
public class DebtImport {

    private Long id;
    private String name;
    private String code;
    private boolean disabled;
    private String debtImportFormat;

}
