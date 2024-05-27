package fintech.bo.spain.alfa.dc.rescheduling;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import fintech.bo.components.Formats;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ReschedulingItemComponent extends HorizontalLayout {

    private final Binder<ReschedulingItem> binder;
    private final Runnable itemChangedListener;
    private final Runnable addNewItemListener;
    private final Consumer<ReschedulingItemComponent> removeItemListener;

    private TextField total;

    public ReschedulingItemComponent(ReschedulingItem item,
                                     Runnable itemChangedListener,
                                     Runnable addNewItemListener,
                                     Consumer<ReschedulingItemComponent> removeItemListener) {
        this.itemChangedListener = itemChangedListener;
        this.addNewItemListener = addNewItemListener;
        this.removeItemListener = removeItemListener;

        this.binder = new Binder<>(ReschedulingItem.class);
        binder.setBean(item);
        build();
    }

    ReschedulingItem getItem() {
        return binder.getBean();
    }

    void setTotal(BigDecimal value) {
        total.setValue(value.toString());
    }

    private void build() {
        DateField dueDate = new DateField();
        dueDate.setDateFormat(Formats.DATE_FORMAT);
        dueDate.setWidth(120, Unit.PIXELS);
        binder.bind(dueDate, "dueDate");

        TextField principal = createDecimalField(binder, "principal");
        TextField interest = createDecimalField(binder, "interest");
        TextField penalty = createDecimalField(binder, "penalty");
        total = createDecimalField(binder, "total");
        total.setReadOnly(true);

        Button removeButton = new Button("-");
        removeButton.addClickListener(event -> removeItemListener.accept(this));

        Button addButton = new Button("+");
        addButton.addClickListener(event -> addNewItemListener.run());

        addComponent(dueDate);
        addComponent(principal);
        addComponent(interest);
        addComponent(penalty);
        addComponent(total);
        addComponent(removeButton);
        addComponent(addButton);
    }

    private TextField createDecimalField(Binder<ReschedulingItem> binder, String fieldName) {
        TextField field = new TextField();
        field.setWidth(100, Unit.PIXELS);
        field.setValue("0");
        binder.forField(field)
            .withConverter(new StringToBigDecimalConverter("Must be a number"))
            .bind(fieldName);
        field.addValueChangeListener(event -> {
            if (isBlank(event.getValue())) {
                ((TextField) event.getComponent()).setValue("0");
            }
            itemChangedListener.run();
        });

        return field;
    }

}
