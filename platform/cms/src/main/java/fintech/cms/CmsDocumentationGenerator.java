package fintech.cms;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CmsDocumentationGenerator implements AutoCloseable {

    private static final String HEADER_DELIMITER = "--------------------------------------------------------------";
    private static final String FIELD_DOC_TEMPLATE = "{{%s.%s%s}}";
    private static final String SIMPLE_TEMPLATE = "{{%s}}";

    private static final String FOR_BEGIN_TEMPLATE = "{%% for val in %s.%s %%}";
    private static final String FOR_END = "{% endfor %}";

    private final StringWriter sw;
    private final PrintWriter pw;

    public CmsDocumentationGenerator() {
        sw = new StringWriter();
        pw = new PrintWriter(sw);
    }

    public String generateContextDocumentation(Map<String, Object> context) {
        context.forEach((key, value) -> {
            printHeader(key);
            if (isSimpleType(value.getClass()))
                print(key);
            else if (value instanceof Map) {
                ((Map) value).forEach((k, v) -> {
                    if (isSimpleType(v.getClass()))
                        print(String.format("%s['%s']", key, k));
                    else
                        ReflectionUtils.doWithFields(v.getClass(),
                            f -> printField(0, String.format("%s['%s']", key, k), f, value));
                });
            } else
                ReflectionUtils.doWithFields(value.getClass(), field -> printField(0, key, field, value));
        });
        return sw.toString();
    }

    private void printHeader(String key) {
        pw.println(HEADER_DELIMITER);
        pw.println(key);
        pw.println(HEADER_DELIMITER);
    }

    private void printField(int lvl, String key, Field field, Object val) {
        field.setAccessible(true);
        Class<?> type = field.getType();

        pw.print(tabs(lvl));
        if (isSimpleType(type))
            docForSimpleType(key, field);
        else if (type.isAssignableFrom(List.class))
            docForList(lvl, key, field, val);
        else if (type.isAssignableFrom(Map.class))
            docForMap(lvl, key, field, val);
        else if (isPojo(type))
            docForPojo(lvl, key, field, val);
        else
            throw new UnsupportedOperationException(String.format("Class %s not supported in CMS models", type.getName()));

    }

    private boolean isPojo(Class<?> type) {
        return Optional.ofNullable(type.getPackage())
            .map(Package::getName)
            .orElse("")
            .startsWith("fintech");
    }

    private void print(String key) {
        pw.println(String.format(SIMPLE_TEMPLATE, key));
    }

    private void docForSimpleType(String key, Field field) {
        String formatFunction = resolveFormatFunction(field.getType());
        pw.println(String.format(FIELD_DOC_TEMPLATE, key, field.getName(), formatFunction));
    }

    private void docForList(int lvl, String key, Field field, Object val) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        pw.println(String.format(FOR_BEGIN_TEMPLATE, key, field.getName()));
        ReflectionUtils.doWithFields((Class) type.getActualTypeArguments()[0],
            f -> printField(lvl + 1, "val", f, val));
        pw.println(FOR_END);
    }

    private void docForMap(int lvl, String key, Field field, Object val) {
        Map<?, ?> map = (Map) ReflectionUtils.getField(field, val);
        map.forEach((k, v) -> ReflectionUtils.doWithFields(v.getClass(),
            f -> printField(lvl, String.format("%s.%s['%s']", key, field.getName(), k), f, val)));
    }

    private void docForPojo(int lvl, String key, Field field, Object val) {
        Object fieldValue = ReflectionUtils.getField(field, val);
        Optional.ofNullable(fieldValue)
            .ifPresent(v -> ReflectionUtils.doWithFields(v.getClass(),
                f -> printField(lvl, String.join(".", key, field.getName()), f, v)));
    }

    private String resolveFormatFunction(Class<?> type) {
        if (type.isAssignableFrom(BigDecimal.class))
            return " | numberformat(currencyFormat)";
        else if (type.isAssignableFrom(LocalDate.class))
            return " | ldate(dateFormat)";
        else if (type.isAssignableFrom(LocalDateTime.class))
            return " | ldatetime(dateTimeFormat)";
        else
            return "";
    }

    private boolean isSimpleType(Class<?> type) {
        return BeanUtils.isSimpleValueType(type) || type.isAssignableFrom(LocalDate.class)
            || type.isAssignableFrom(String.class) || type.isAssignableFrom(LocalDateTime.class);
    }

    private String tabs(int number) {
        return StringUtils.repeat('\t', number);
    }

    @Override
    public void close() throws Exception {
        pw.close();
    }
}
