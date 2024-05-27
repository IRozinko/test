package fintech.lending.core.application.commands;

import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Value
public class SaveParamsCommand {

    @NotNull
    Long id;

    @NotBlank
    String key;

    @NotBlank
    String value;
}
