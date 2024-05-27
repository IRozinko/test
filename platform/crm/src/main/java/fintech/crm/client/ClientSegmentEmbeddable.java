package fintech.crm.client;

import fintech.TimeMachine;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "segment")
@Embeddable
public class ClientSegmentEmbeddable {

    @Column(nullable = false)
    private String segment;

    @Column(nullable = false)
    private LocalDateTime addedAt = TimeMachine.now();

    public ClientSegment toValueObject() {
        ClientSegment val = new ClientSegment();
        val.setSegment(this.segment);
        val.setAddedAt(this.addedAt);
        return val;
    }
}
