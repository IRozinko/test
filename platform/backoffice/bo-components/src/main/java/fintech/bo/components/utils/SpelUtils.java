package fintech.bo.components.utils;

import org.jooq.Record;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class SpelUtils {

    public static Boolean checkExpression(List<Field> fields, Object object, String expression) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        return checkExpression(fields, object.getClass(), object, expression);
    }

    public static Boolean checkExpression(List<Field> fields, Class fieldsClass, Object object, String expression) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = resultValueContext(fields, fieldsClass, object);
        return parser.parseExpression(expression).getValue(context, Boolean.class);
    }

    public static Boolean checkJooqExpression(List<org.jooq.Field> fields, Record record, String expression) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = jooqResultValueContext(fields, record);
        return parser.parseExpression(expression).getValue(context, Boolean.class);
    }

    public static String renderTemplate(List<Field> fields, Object object, String template) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        return renderTemplate(fields, object.getClass(), object, template);
    }

    public static String renderTemplate(List<Field> fields, Class fieldsClass, Object object, String template) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = resultValueContext(fields, fieldsClass, object);
        return parser.parseExpression(template).getValue(context, String.class);
    }

    public static String renderJooqTemplate(List<org.jooq.Field> fields, Record record, String template) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = jooqResultValueContext(fields, record);
        return parser.parseExpression(template).getValue(context, String.class);
    }

    private static StandardEvaluationContext resultValueContext(List<Field> fields, Class fieldsClass, Object object) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (Field field : fields) {
            Object value = new PropertyDescriptor(field.getName(), fieldsClass).getReadMethod().invoke(object);
            context.setVariable(field.getName(), value);
        }

        return context;
    }

    private static StandardEvaluationContext jooqResultValueContext(List<org.jooq.Field> fields, Record record) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (org.jooq.Field field : fields) {
            context.setVariable(field.getName(), field.getValue(record));
        }

        return context;
    }
}
