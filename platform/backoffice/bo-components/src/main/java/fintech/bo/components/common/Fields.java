package fintech.bo.components.common;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.server.Sizeable;
import fintech.bo.components.Formats;
import fintech.bo.components.common.field.IntegerRange;
import fintech.bo.components.common.field.IntegerRangeField;

public final class Fields {

    public static DateRangeField dateRangeField(String caption) {
        DateRangeField field = new DateRangeField(caption);
        field.getBeginDateField().setWidth(125, Sizeable.Unit.PIXELS);
        field.getBeginDateField().setDateFormat(Formats.DATE_FORMAT);
        field.getBeginDateField().setPlaceholder("From");
        field.getEndDateField().setWidth(125, Sizeable.Unit.PIXELS);
        field.getEndDateField().setDateFormat(Formats.DATE_FORMAT);
        field.getEndDateField().setPlaceholder("To");
        return field;
    }

    public static IntegerRangeField integerRangeField(String caption) {
        return new IntegerRangeField(caption);
    }
}
