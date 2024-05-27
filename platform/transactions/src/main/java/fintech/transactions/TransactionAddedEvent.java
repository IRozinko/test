package fintech.transactions;


import lombok.Value;

@Value
public class TransactionAddedEvent {

    private final Transaction transaction;
}
