package fintech.marketing.bo.components;

import com.google.common.collect.Maps;
import com.vaadin.ui.Label;
import fintech.bo.components.dc.NumberField;

import java.util.Map;

public class AudiencePeriodPredicateComponent extends AudienceConditionComponentAbs {

    private NumberField fromValue;
    private NumberField toValue;
    private final Integer minValue;


    public AudiencePeriodPredicateComponent(String label, String type, Integer minValue) {
        super(label, type);
        this.minValue = minValue;
    }

    public void initComponents() {
        addComponent(new Label("from"));

        fromValue = NumberField.builder()
            .setMinValue(minValue)
            .setRequired(true)
            .build();

        addComponent(fromValue.getComponent());
        addComponent(new Label("to"));

        toValue = NumberField.builder()
            .setMinValue(minValue)
            .build();
        addComponent(toValue.getComponent());
    }

    @Override
    public void setValues() {
        fromValue.setValue((Integer) params.getOrDefault("from", 3));
        toValue.setValue((Integer) params.getOrDefault("to", 5));
    }

    @Override
    public boolean isValid() {
        return fromValue.isValueValid() && toValue.isValueValid();
    }

    @Override
    public void validate() {
        fromValue.validate();
        toValue.validate();
    }



    @Override
    public Map<String, Object> getParams() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("from", fromValue.getValueOrNull());
        params.put("to", toValue.getValueOrNull());
        return params;
    }
}
