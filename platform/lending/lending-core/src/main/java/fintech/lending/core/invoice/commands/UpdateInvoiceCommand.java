package fintech.lending.core.invoice.commands;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class UpdateInvoiceCommand {

    private Long invoiceId;

    List<GeneratedInvoice.GeneratedInvoiceItem> items = Lists.newArrayList();

    private boolean generateFile = false;

    private boolean sendFile = false;

}
