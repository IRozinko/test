package fintech.marketing.bo.components;

import com.google.common.collect.Maps;
import fintech.bo.components.dc.NumberField;

import java.util.Map;

public class AudienceNumberValuePredicateComponent extends AudienceConditionComponentAbs {

    private NumberField value;

    public AudienceNumberValuePredicateComponent(String label, String type) {
        super(label, type);
    }

    public void initComponents() {
        value = NumberField.builder()
            .setMinValue(0)
            .setRequired(true)
            .build();
        addComponent(value.getComponent());
    }

    @Override
    public void setValues() {
        value.setValue((Integer) params.getOrDefault("value", 0));
    }

    @Override
    public boolean isValid() {
        return value.isValueValid();
    }

    @Override
    public void validate() {
        value.validate();
    }

    @Override
    public Map<String, Object> getParams() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("value", value.getValueOrNull());
        return params;
    }
}
