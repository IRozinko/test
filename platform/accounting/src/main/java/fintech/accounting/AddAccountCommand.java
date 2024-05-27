package fintech.accounting;

import lombok.Data;

@Data
public class AddAccountCommand {

    private String code;
    private String name;

    public AddAccountCommand(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public AddAccountCommand() {
    }
}
