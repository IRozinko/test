package fintech.spain.alfa.product.affiliate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = DeprecatedRejectedClientApplicationValidator.class)
@Deprecated
public @interface DeprecatedRejectedClientApplication {

    String message() default "org.hibernate.validator.constraints.RejectedClientApplication.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
