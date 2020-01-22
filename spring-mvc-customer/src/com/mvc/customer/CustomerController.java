package com.mvc.customer;

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomerController {

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class,editor);
	}
	@RequestMapping("/")
	public String customerMenu() {
		return "main-menu";
	}
	
	@RequestMapping("/registration")
	public String customerRegistration(Model model) {
		Customer customer = new Customer();
		model.addAttribute("customer", customer);
		return "customer-registration";
	}
	@RequestMapping("/confirm")
	public String customerConfirm(@Valid @ModelAttribute("customer") Customer customer, BindingResult theBindingResult) {
		System.out.println("firstName:"+" "+customer.getFirstName()+"\t"+"lastName:"+" "+customer.getLastName()+""+customer.getItemCode());
		if(theBindingResult.hasErrors())
		{
			return "customer-registration";
		}else
		{
			return "customer-confirm";
		}
		

	}
}
