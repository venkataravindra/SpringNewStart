package com.mvc.customer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemCodeConstraintValidator 
implements ConstraintValidator<ItemCode, String> {

private String itemPrefix;

@Override
public void initialize(ItemCode theItemCode) {
	itemPrefix = theItemCode.value();
}

@Override
public boolean isValid(String theCode, 
					ConstraintValidatorContext theConstraintValidatorContext) {

	boolean result;
	
	if (theCode != null) {
		result = theCode.startsWith(itemPrefix);
	}
	else {
		result = true;
	}
	
	return result;
}
}

