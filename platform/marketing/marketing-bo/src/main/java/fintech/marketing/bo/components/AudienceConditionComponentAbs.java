package fintech.marketing.bo.components;

import com.vaadin.data.HasValue;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import java.util.Map;

public abstract class AudienceConditionComponentAbs extends HorizontalLayout {

    protected Map<String, Object> params;
    private final CheckBox checkBox;
    public final String type;
    public final String label;

    public AudienceConditionComponentAbs(String label, String type) {
        setSpacing(true);
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        this.type = type;
        this.label = label;
        checkBox = new CheckBox();
        checkBox.setValue(false);
        addComponent(checkBox);
        addComponent(new Label(label));
        initComponents();
        toggleElements(false);
        checkBox.addValueChangeListener((HasValue.ValueChangeListener<Boolean>) event -> toggleElements(event.getValue()));
    }

    public void initComponents() {

    }

    public abstract void setValues();

    private void toggleElements(boolean enabled) {
        for (int i = 1; i < getComponentCount(); i++) {
            getComponent(i).setEnabled(enabled);
        }
        validate();
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
        checkBox.setValue(true);
        setValues();
        validate();
    }

    public String getType() {
        return type;
    }

    public abstract boolean isValid();

    public abstract void validate();

    public abstract Map<String, Object> getParams();

    public boolean isEnabledPredicate() {
        return checkBox.getValue();
    }
}
