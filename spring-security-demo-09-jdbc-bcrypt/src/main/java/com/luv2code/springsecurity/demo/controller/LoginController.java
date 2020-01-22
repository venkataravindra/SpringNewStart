package com.luv2code.springsecurity.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/showMyLoginPage")
	public String ShowMyLoginPage() {
		//return "plain-login";
		return "fancy-login";
	}
	// add arequest mapping for /access denied 
	@GetMapping("/access-denied")
	public String ShowAccessDenied() {
		//return "plain-login";
		return "access-denied";
	}
	
}
