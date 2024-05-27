package fintech.spain.unnax.transfer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum TransferAutoType {

    STANDARD(0), SAME_DAY(1);

    private int numeric;

    public static TransferAutoType findByNumeric(int val) {
        return Stream.of(TransferAutoType.values())
            .filter(v -> v.getNumeric() == val)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("TransferAutoType with numeric value %d doesn't exist", val)));
    }

}
