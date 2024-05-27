package fintech.bo.components;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IdNameDetails {

    private Long id;
    private String name;
}
