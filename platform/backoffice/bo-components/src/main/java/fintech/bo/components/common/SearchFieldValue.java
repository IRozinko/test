package fintech.bo.components.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class SearchFieldValue {

    private String field;
    private String value;

    public boolean isNotBlank() {
        return StringUtils.isNotBlank(field) && StringUtils.isNotBlank(value);
    }

    public void removeWhiteSpaces() {
        value = StringUtils.deleteWhitespace(value);
    }
}
