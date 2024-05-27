package fintech.lending.revolving.settings;

import lombok.Data;

@Data
public class RevolvingInvoiceSettings {

    private int dueDays;
    private int gracePeriod;

}
