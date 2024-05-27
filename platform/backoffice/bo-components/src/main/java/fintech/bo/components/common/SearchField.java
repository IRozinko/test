package fintech.bo.components.common;

import com.google.common.collect.ImmutableList;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.Collection;

import static fintech.bo.components.common.SearchFieldOptions.ALL;

public class SearchField extends CustomField<SearchFieldValue> {

    private CssLayout layout;
    private TextField search;
    private ComboBox<String> fieldSelect;

    public SearchField() {
        search = new TextField();
        search.setPlaceholder("Search...");
        search.setWidth(200, Unit.PIXELS);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.setValueChangeTimeout(750);
        search.focus();

        fieldSelect = new ComboBox<>();
        fieldSelect.setItems(ImmutableList.of(ALL));
        fieldSelect.setSelectedItem(ALL);
        fieldSelect.setEmptySelectionAllowed(false);

        layout = new CssLayout(search, fieldSelect);
        layout.addStyleName("v-component-group");

        search.addValueChangeListener(val ->
            fireEvent(createValueChange(new SearchFieldValue(fieldSelect.getValue(), val.getValue()), val.isUserOriginated()))
        );
        fieldSelect.addValueChangeListener(val -> {
            if (val.isUserOriginated() && !search.isEmpty())
                fireEvent(createValueChange(new SearchFieldValue(val.getValue(), search.getValue()), val.isUserOriginated()));
        });
    }

    @Override
    protected void doSetValue(SearchFieldValue value) {
        createValueChange(value, true);
        this.fieldSelect.setValue(value.getField());
        this.search.setValue(value.getValue());
    }

    @Override
    protected Component initContent() {
        return layout;
    }

    @Override
    public SearchFieldValue getValue() {
        return new SearchFieldValue(fieldSelect.getValue(), search.getValue());
    }

    public void addFieldOptions(Collection<String> fields) {
        ArrayList<String> fieldsNames = new ArrayList<>(fields);
        fieldsNames.add(ALL);
        fieldSelect.setItems(fieldsNames);
        fieldSelect.setSelectedItem(fieldsNames.get(0));
    }


}
