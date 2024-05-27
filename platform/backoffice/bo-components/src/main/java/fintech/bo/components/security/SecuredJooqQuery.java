package fintech.bo.components.security;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecuredJooqQuery {

    @AliasFor("permissions")
    String[] value() default {};

    @AliasFor("value")
    String[] permissions() default {};

    String condition() default "";

    String template() default "";

    String[] fields() default {};
}
