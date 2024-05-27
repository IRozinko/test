package fintech.spain.unnax.callback.model;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BankStatementsUploadedData implements CallbackData {

    private String requestCode;
    private String link;
    private String errorCode;
    private String errorMessage;
}
