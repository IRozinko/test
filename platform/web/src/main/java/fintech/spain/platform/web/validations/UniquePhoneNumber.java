package fintech.spain.platform.web.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniquePhoneNumberValidator.class)
public @interface UniquePhoneNumber {

    String message() default "org.hibernate.validator.constraints.InvalidValue.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
