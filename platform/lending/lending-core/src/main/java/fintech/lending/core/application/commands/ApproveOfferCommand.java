package fintech.lending.core.application.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ApproveOfferCommand {

    private Long id;

    private LocalDateTime offerApprovedAt;

    private String offerApprovedBy;

    private String offerApprovedFromIpAddress;
}
