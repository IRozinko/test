package fintech.spain.alfa.bo.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveTransactionCategoryRequest {

    @NotNull
    private Long transactionId;

    @NotNull
    private Long wealthinessId;

    private String category;

}
