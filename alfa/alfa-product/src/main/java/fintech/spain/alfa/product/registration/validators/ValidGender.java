package fintech.spain.alfa.product.registration.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface ValidGender {

    String message() default "org.hibernate.validator.constraints.InvalidValue.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
