package fintech.spain.alfa.web.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordHashValidator.class)
public @interface PasswordHash {

    String message() default "org.hibernate.validator.constraints.InvalidValue.message";

    Class<?>[] groups() default {};

    /**
     * If this flag is set to true then in case of temporary password the validation of hash will be skipped
     * @return
     */
    boolean skipTemporaryValidation() default false;

    Class<? extends Payload>[] payload() default {};
}
