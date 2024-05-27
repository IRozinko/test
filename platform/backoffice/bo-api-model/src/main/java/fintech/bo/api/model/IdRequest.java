package fintech.bo.api.model;


import lombok.NonNull;
import lombok.Value;

@Value
public class IdRequest {
    @NonNull
    private final Long id;
}
