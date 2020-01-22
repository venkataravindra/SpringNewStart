package com.dependencyinjection.demo;

public class BaseballCoach implements Coach{

	//define a private field for the dependency
	private FortuneService fortuneService;
	//define a constructor for dependency injection
	public BaseballCoach(FortuneService fortuneService) {
		super();
		this.fortuneService = fortuneService;
	}

	public String getDailyWorkOut() {
		
		return "Spend 30 minutes on batting practice";
	}

	@Override
	public String getDailyFortune() {
		//usemy fortune service to get fortune 
		return fortuneService.getFortune();
	}

}
