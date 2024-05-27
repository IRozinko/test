package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class InvoiceInfo {

    private Long id;
    private String number;
    private LocalDateTime invoiceDate;
    private String fileId;

}
