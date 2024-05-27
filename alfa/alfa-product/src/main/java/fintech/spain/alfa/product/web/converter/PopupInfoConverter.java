package fintech.spain.alfa.product.web.converter;

import com.google.common.base.Converter;
import fintech.spain.alfa.product.web.db.PopupEntity;
import fintech.spain.alfa.product.web.model.PopupInfo;

public class PopupInfoConverter extends Converter<PopupEntity, PopupInfo> {

    public static final PopupInfoConverter INSTANCE = new PopupInfoConverter();

    @Override
    protected PopupInfo doForward(PopupEntity entity) {
        return new PopupInfo()
            .setId(entity.getId())
            .setClientId(entity.getClientId())
            .setType(entity.getType())
            .setResolution(entity.getResolution())
            .setValidUntil(entity.getValidUntil())
            .setResolvedAt(entity.getResolvedAt())
            .setAttributes(entity.getAttributes());
    }

    @Override
    protected PopupEntity doBackward(PopupInfo info) {
        throw new UnsupportedOperationException();
    }
}
