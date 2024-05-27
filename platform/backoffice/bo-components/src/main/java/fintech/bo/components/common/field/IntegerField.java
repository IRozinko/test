package fintech.bo.components.common.field;

import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.TextField;
import org.apache.commons.lang3.StringUtils;

public class IntegerField extends TextField implements HasValue.ValueChangeListener<String> {

    private String lastValue;

    public IntegerField(String caption) {
        super(caption);
        setValueChangeMode(ValueChangeMode.EAGER);
        addValueChangeListener(this);
        lastValue = "";
    }

    public IntegerField() {
        this(null);
    }

    @Override
    public void valueChange(ValueChangeEvent<String> event) {
        String text = event.getValue();
        if (StringUtils.isNumeric(text)) {
            lastValue = text;
        } else {
            setValue(StringUtils.isBlank(text) ? StringUtils.EMPTY : lastValue);
        }
    }
}
