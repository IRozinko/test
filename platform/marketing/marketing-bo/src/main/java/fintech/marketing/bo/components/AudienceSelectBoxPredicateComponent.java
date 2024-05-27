package fintech.marketing.bo.components;

import com.google.common.collect.Maps;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.ui.ComboBox;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static com.vaadin.data.ValidationResult.ok;

public class AudienceSelectBoxPredicateComponent extends AudienceConditionComponentAbs {

    private ComboBox<String> value;
    private Binder<StringBean> binder;

    public AudienceSelectBoxPredicateComponent(String label, String type, List<String> values) {
        super(label, type);
        value.setItems(values);
        if (!values.isEmpty()) {
            value.setSelectedItem(values.get(0));
        }
    }

    public void initComponents() {
        value = new ComboBox<>();

        value.setTextInputAllowed(false);
        value.setEmptySelectionAllowed(false);

        value.setWidth(200, Unit.PIXELS);
        addComponent(value);

        binder = new Binder<>();
        binder
            .forField(value)
            .withValidator((value, ctx) -> this.value.isEnabled() && StringUtils.isEmpty(value) ? ValidationResult.error("Empty value") : ok())
            .bind(StringBean::getValue, StringBean::setValue);
    }

    @Override
    public void setValues() {
        value.setSelectedItem((String) params.get("value"));
    }

    @Override
    public boolean isValid() {
        return binder.isValid();
    }

    @Override
    public void validate() {
        binder.validate();
    }

    @Override
    public Map<String, Object> getParams() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("value", value.getValue());
        return params;
    }

    @Data
    private static class StringBean {
        private String value;
    }
}
