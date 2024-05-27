package fintech.bo.components.security;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecuredTab {
    @AliasFor("permissions")
    String[] value() default {};

    @AliasFor("value")
    String[] permissions() default {};

    String condition() default "";
}
