package fintech.bo.components.settings;

import fintech.bo.components.JsonUtils;
import fintech.bo.db.jooq.settings.tables.records.PropertyRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Property {

    private Long id;

    private String name;

    private String originalName;

    private String type;

    private Boolean booleanValue;

    private Long numberValue;

    private BigDecimal decimalValue;

    private LocalDateTime dateTimeValue;

    private LocalDate dateValue;

    private String textValue;

    public Property(PropertyRecord record) {
        id = record.getId();
        name = record.getName();
        originalName = record.getName();

        type = record.getType();
        booleanValue = record.getBooleanValue();
        numberValue = record.getNumberValue();
        decimalValue = record.getDecimalValue();
        dateTimeValue = record.getDateTimeValue();
        dateValue = record.getDateValue();
        textValue = record.getTextValue();
    }

    public <T> T getValue() {
        if (booleanValue != null)
            return (T) booleanValue;
        if (numberValue != null)
            return (T) numberValue;
        if (decimalValue != null)
            return (T) decimalValue;
        if (dateTimeValue != null)
            return (T) dateTimeValue;
        if (dateValue != null)
            return (T) dateValue;
        if (textValue != null)
            return (T) textValue;

        return (T) "";
    }

    public void setValue(Map<String, Object> jsonRoot) {
        switch (PropertyType.valueOf(type)) {
            case BOOLEAN:
                setBooleanValue((Boolean) jsonRoot.get(name));
                break;
            case DATE:
                setDateValue((LocalDate) jsonRoot.get(name));
                break;
            case DATETIME:
                setDateTimeValue((LocalDateTime) jsonRoot.get(name));
                break;
            case DECIMAL:
                setDecimalValue((BigDecimal) jsonRoot.get(name));
                break;
            case NUMBER:
                setNumberValue((Long) jsonRoot.get(name));
                break;
            case TEXT: {
                if (jsonRoot.containsKey(name))
                    setTextValue((String) jsonRoot.get(name));
                else
                    setTextValue(JsonUtils.writeValueAsString(jsonRoot));
                break;
            }
        }
    }

}
