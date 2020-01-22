package com.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApllicationController {
	
	@GetMapping("/")
	public String displayStartPage()
	{
		return "start";
	}

}
