package fintech.spain.alfa.web.models;

import fintech.web.api.models.AmortizationPayment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
public class DormantsLocOffer {

    private Long applicationId;
    private BigDecimal creditLimit;
    private AttachmentData loanAgreementAttachment;
    private AttachmentData standardInformationAttachment;
    private List<AmortizationPayment> payments = newArrayList();
}
