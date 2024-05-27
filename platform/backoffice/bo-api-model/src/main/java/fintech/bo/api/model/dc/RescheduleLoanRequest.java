package fintech.bo.api.model.dc;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class RescheduleLoanRequest {

    @NotNull
    private Long loanId;

    @NotNull
    private ReschedulingPreviewResponse reschedulingPreview;

}
