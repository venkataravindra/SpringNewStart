package com.mvc.customer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Constraint(validatedBy = ItemCodeConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemCode {

	// define default course code
	public String value() default "ITM";
	
	// define default error message
	public String message() default "must start with ITM";
	
	// define default groups
	public Class<?>[] groups() default {};
	// define default payloads
	public Class<? extends Payload>[] payload() default {};
}
