package fintech.spain.alfa.product.web.model.event;

import fintech.spain.alfa.product.web.model.PopupInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PopupResolvedEvent {
    PopupInfo popupInfo;
}
