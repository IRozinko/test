package fintech.cms;


import lombok.ToString;
import lombok.Value;

@Value
@ToString(of = "name")
public class Pdf {
    private final String name;
    private final byte[] content;
}
