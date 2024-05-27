package fintech.bo.api.model.dc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class ChangeCompanyRequest {

    @NotNull
    private Long debtId;

    @NotNull
    private String company;
}
