package fintech.payments.settigs;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentsSettings {

    private boolean unnaxEnabled;
    private boolean autoExportEnabled;

}
