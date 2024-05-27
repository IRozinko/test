package fintech.spain.unnax.db;

import fintech.db.BaseEntity;
import fintech.spain.unnax.callback.model.CallbackRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "callback", schema = Entities.SCHEMA)
@NoArgsConstructor
public class CallbackEntity extends BaseEntity {

    private String data;
    private String event;
    private LocalDateTime date;
    private String signature;
    private String responseId;
    private String traceIdentifier;
    private String environment;

    public CallbackEntity(CallbackRequest request) {
        data = request.getDataAsText();
        event = request.getTriggeredEvent();
        date = request.getDate();
        signature = request.getSignature();
        responseId = request.getResponseId();
        traceIdentifier = request.getTraceIdentifier();
        environment = request.getEnvironment();
    }
}
