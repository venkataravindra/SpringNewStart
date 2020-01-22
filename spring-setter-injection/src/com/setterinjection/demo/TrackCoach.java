package com.setterinjection.demo;


public class TrackCoach implements Coach {

private FortuneService fortuneService;
	
	public TrackCoach(FortuneService fortuneService) {
		super();
		this.fortuneService = fortuneService;
	}

	public String getDailyWorkOut() {
		return "Run a hard 5K";
	}

	@Override
	public String getDailyFortune() {
		
		return "Just do it:"+fortuneService.getFortune();
	}
}
