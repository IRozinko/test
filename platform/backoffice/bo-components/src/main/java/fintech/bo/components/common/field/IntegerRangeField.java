package fintech.bo.components.common.field;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import org.apache.commons.lang3.StringUtils;

public class IntegerRangeField extends CustomField<IntegerRange> {

    private final CssLayout root;
    private final IntegerField minIntegerField;
    private final IntegerField maxIntegerField;
    private Registration minIntegerFieldListenerRegistration;
    private Registration maxIntegerFieldListenerRegistration;

    public IntegerRangeField() {
        this.minIntegerField = new IntegerField();
        this.minIntegerField.setPlaceholder("Min");
        this.minIntegerField.setWidth(2, Unit.CM);
        this.maxIntegerField = new IntegerField();
        this.maxIntegerField.setPlaceholder("Max");
        this.maxIntegerField.setWidth(2, Unit.CM);
        this.root = new CssLayout(this.minIntegerField, this.maxIntegerField);
        this.root.addStyleName("v-component-group");
        this.minIntegerField.addValueChangeListener(minIntegerFieldChangeListener());
        this.attachValueChangeListeners();
    }

    public IntegerRangeField(String caption) {
        this();
        this.setCaption(caption);
    }

    private ValueChangeListener<String> minIntegerFieldChangeListener() {
        return (change) -> {
            Integer min = getInteger(change.getValue());
            Integer max = this.getMax();
            if (min != null && max != null && max.compareTo(min) < 0) {
                this.maxIntegerFieldListenerRegistration.remove();
                this.maxIntegerField.clear();
                this.minIntegerFieldListenerRegistration = this.maxIntegerField.addValueChangeListener((event) -> {
                    Integer oldMax = getInteger(event.getOldValue());
                    this.fireEvent(new ValueChangeEvent<>(this, new IntegerRange(this.getMin(), oldMax), event.isUserOriginated()));
                });

            }
        };
    }

    @Override
    protected Component initContent() {
        return this.root;
    }

    @Override
    protected void doSetValue(IntegerRange value) {
        this.setRange(value.getMin(), value.getMax());
    }

    @Override
    public IntegerRange getValue() {
        return new IntegerRange(getMin(), getMax());
    }

    public Integer getMax() {
        return getInteger(maxIntegerField.getValue());
    }

    public Integer getMin() {
        return getInteger(minIntegerField.getValue());
    }

    public void setMim(Integer min) {
        this.minIntegerField.setValue(String.valueOf(min));
    }

    public void setMax(Integer max) {
        this.maxIntegerField.setValue(String.valueOf(max));
    }

    public void setRange(Integer min, Integer max) {
        IntegerRange oldValue = this.getValue();
        this.detachValueChangeListeners();
        this.setMim(min);
        this.setMax(max);
        this.attachValueChangeListeners();
        this.fireEvent(new ValueChangeEvent<>(this, oldValue, false));
    }

    private void detachValueChangeListeners() {
        this.minIntegerFieldListenerRegistration.remove();
        this.maxIntegerFieldListenerRegistration.remove();
    }

    private void attachValueChangeListeners() {
        this.minIntegerFieldListenerRegistration = this.minIntegerField.addValueChangeListener((ValueChangeEvent<String> event) -> {
            Integer oldMin = getInteger(event.getOldValue());
            this.fireEvent(new ValueChangeEvent<>(this, new IntegerRange(oldMin, this.getMax()), event.isUserOriginated()));
        });
        this.maxIntegerFieldListenerRegistration = this.maxIntegerField.addValueChangeListener((event) -> {
            Integer oldMax = getInteger(event.getOldValue());
            this.fireEvent(new ValueChangeEvent<>(this, new IntegerRange(this.getMin(), oldMax), event.isUserOriginated()));
        });
    }

    private Integer getInteger(String text) {
        if (StringUtils.isNumeric(text)) {
            return Integer.valueOf(text);
        } else {
            return null;
        }
    }
}
