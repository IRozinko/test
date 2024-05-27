package fintech.bo.components.security;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static fintech.bo.components.utils.SpelUtils.checkExpression;
import static fintech.bo.components.utils.SpelUtils.checkJooqExpression;
import static fintech.bo.components.utils.SpelUtils.renderJooqTemplate;
import static fintech.bo.components.utils.SpelUtils.renderTemplate;

@Aspect
@Component
public class SecurityAspect {

    private static final String DEFAULT_OBFUSCATED_VALUE = "***";

    @Around("methodsAnnotatedWithSecuredQueryAnnotation()")
    private Object processMethodsAnnotatedWithSecuredQueryAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retVal = joinPoint.proceed();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SecuredQuery securedQuery = method.getAnnotation(SecuredQuery.class);
        if (LoginService.hasPermission(ArrayUtils.addAll(securedQuery.permissions(), securedQuery.value()))) {
            return retVal;
        }

        if (retVal instanceof Collection) {
            for (Object r : (Collection) retVal) {
                applyObfuscation(securedQuery, r);
            }
        } else {
            applyObfuscation(securedQuery, retVal);
        }


        return retVal;
    }

    @Around("methodsAnnotatedWithSecuredJooqQueryAnnotation()")
    private Object processMethodsAnnotatedWithSecuredJooqQueryAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        Result retVal = (Result) joinPoint.proceed();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SecuredJooqQuery securedJooqQuery = method.getAnnotation(SecuredJooqQuery.class);
        if (LoginService.hasPermission(ArrayUtils.addAll(securedJooqQuery.permissions(), securedJooqQuery.value()))) {
            return retVal;
        }

        for (Object o : retVal) {
            applyJooqObfuscation(retVal, securedJooqQuery, (Record) o);
        }

        return retVal;
    }

    @Pointcut("@annotation(fintech.bo.components.security.SecuredQuery)")
    private void methodsAnnotatedWithSecuredQueryAnnotation() {

    }

    @Pointcut("@annotation(fintech.bo.components.security.SecuredJooqQuery)")
    private void methodsAnnotatedWithSecuredJooqQueryAnnotation() {

    }

    private void applyObfuscation(SecuredQuery securedQuery, Object result) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        ArrayList<Field> allFields = new ArrayList<>(Arrays.asList(result.getClass().getDeclaredFields()));

        Boolean conditionResult = checkExpression(allFields, result, securedQuery.condition());
        if (conditionResult) {
            String obfuscatedValue = DEFAULT_OBFUSCATED_VALUE;
            if (StringUtils.isNotBlank(securedQuery.template())) {
                obfuscatedValue = renderTemplate(allFields, result, securedQuery.template());
            }
            for (Field field : allFields) {
                HiddenForSecuredQuery hideAnnotation = field.getAnnotation(HiddenForSecuredQuery.class);
                if (hideAnnotation == null) {
                    continue;
                }
                Method writeMethod = new PropertyDescriptor(field.getName(), result.getClass()).getWriteMethod();
                if (field.getType().equals(String.class)) {
                    writeMethod.invoke(result, obfuscatedValue);
                } else {
                    writeMethod.invoke(result, new Object[]{null});
                }
            }
        }
    }

    private void applyJooqObfuscation(Result retVal, SecuredJooqQuery securedJooqQuery, Record record) {
        List<org.jooq.Field> allFields = Arrays.asList(retVal.fields());
        Boolean conditionResult = checkJooqExpression(allFields, record, securedJooqQuery.condition());
        if (conditionResult && (securedJooqQuery.fields().length > 0)) {
            String obfuscatedValue = DEFAULT_OBFUSCATED_VALUE;
            if (StringUtils.isNotBlank(securedJooqQuery.template())) {
                obfuscatedValue = renderJooqTemplate(allFields, record, securedJooqQuery.template());
            }
            for (String field : securedJooqQuery.fields()) {
                org.jooq.Field jooqField = retVal.field(field);
                if (jooqField == null) {
                    continue;
                }
                if (jooqField.getDataType().getType().equals(String.class)) {
                    record.setValue(jooqField, obfuscatedValue);
                } else {
                    record.setValue(jooqField, null);
                }
            }
        }
    }
}
