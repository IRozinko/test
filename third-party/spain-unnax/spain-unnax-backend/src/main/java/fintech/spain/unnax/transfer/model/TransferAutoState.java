package fintech.spain.unnax.transfer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferAutoState {

    public static final String NEW = "1";
    public static final String TRANSFERED = "2";
    public static final String COMPLETED = "3";
    public static final String ERROR = "4";
    public static final String CANCELED = "5";

}
