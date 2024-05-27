package fintech.spain.alfa.product.web.spi;

import fintech.spain.alfa.product.web.model.PopupInfo;
import fintech.spain.alfa.product.web.model.PopupResolution;
import fintech.spain.alfa.product.web.model.PopupType;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface PopupService {

    List<PopupInfo> getActual(long clientId);

    PopupInfo show(long clientId, PopupType type);

    PopupInfo show(long clientId, PopupType type, Duration expiration);

    PopupInfo show(long clientId, PopupType type, Duration expiration, Map<String, String> attributes);

    void resolve(long popupId, PopupResolution resolution);

    void markAsExhausted(long clientId, PopupType popupType);
}
