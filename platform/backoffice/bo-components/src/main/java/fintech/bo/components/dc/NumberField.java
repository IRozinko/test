package fintech.bo.components.dc;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.Optional;

import static com.vaadin.data.ValidationResult.error;
import static com.vaadin.data.ValidationResult.ok;
import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static com.vaadin.shared.ui.ValueChangeMode.LAZY;
import static java.lang.Integer.parseInt;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class NumberField {

    private final TextField input;
    private final Binder<IntegerBean> binder;

    public static NumberFieldBuilder builder() {
        return new NumberFieldBuilder();
    }

    private NumberField(TextField input, Binder<IntegerBean> binder) {
        this.input = input;
        this.binder = binder;
    }

    public boolean isValueValid() {
        return binder.isValid();
    }

    public Integer getValueOrNull() {
        return isNotBlank(this.input.getValue()) && isValueValid() ? parseInt(this.input.getValue()) : null;
    }

    public Optional<Integer> getValue() {
        return isNotBlank(this.input.getValue()) && isValueValid() ? Optional.of(parseInt(this.input.getValue())) : Optional.empty();
    }

    public void validate() {
        binder.validate();
    }

    public void clear() {
        input.clear();
    }

    public void setValue(Integer val) {
        input.setValue(val == null ? "" : val.toString());
    }

    public Component getComponent() {
        return input;
    }

    @Data
    private class IntegerBean {
        private Integer value;
    }

    @Setter
    @Accessors(chain = true)
    public static class NumberFieldBuilder {

        private static final String CONVERSION_ERROR_MESSAGE = "Must be integer";

        private int width = 65;
        private String caption;
        private Integer maxValue;
        private Integer minValue;
        private Integer defaultValue;
        private boolean required;
        private HasValue.ValueChangeListener listener;

        @Setter(PRIVATE)
        private TextField input;

        @SuppressWarnings("unchecked")
        public NumberField build() {
            input = new TextField(caption);
            input.setValueChangeMode(LAZY);
            input.setWidth(width, PIXELS);
            if (nonNull(defaultValue)) {
                input.setValue(defaultValue.toString());
            }
            if (nonNull(listener)) {
                input.addValueChangeListener(listener);
            }
            Binder<IntegerBean> binder = (Objects.isNull(maxValue) && Objects.isNull(minValue)) ? createSimpleBinder() : createLimitsBinder();
            return new NumberField(input, binder);
        }

        private Binder<IntegerBean> createSimpleBinder() {
            Binder<IntegerBean> binder = new Binder<>();
            binder.forField(input)
                .withConverter(new StringToIntegerConverter(CONVERSION_ERROR_MESSAGE))
                .bind(IntegerBean::getValue, IntegerBean::setValue);
            return binder;
        }

        private Binder<IntegerBean> createLimitsBinder() {
            Binder<IntegerBean> binder = new Binder<>();
            binder.forField(input)
                .withConverter(new StringToIntegerConverter(CONVERSION_ERROR_MESSAGE))
                .withValidator((value, context) -> {
                    if(!input.isEnabled()) {
                        return ok();
                    }
                    if (!required && Objects.isNull(value)) {
                        return ok();
                    }
                    if (required && Objects.isNull(value)) {
                        return error("Required");
                    } else if (maxValue != null && (value > maxValue)) {
                        return error("Max value is " + maxValue);
                    } else if (minValue != null && value < minValue) {
                        return error("Min value is " + minValue);
                    }
                    return ok();
                })
                .bind(IntegerBean::getValue, IntegerBean::setValue);
            return binder;
        }
    }
}
