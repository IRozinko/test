package fintech.spain.alfa.product.affiliate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = UniqueClientContactsValidator.class)
public @interface UniqueClientContacts {

    String message() default "org.hibernate.validator.constraints.InvalidValue.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
