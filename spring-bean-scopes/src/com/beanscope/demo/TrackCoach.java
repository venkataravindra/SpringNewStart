package com.beanscope.demo;



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
	//add an init method
	public void doMyStartupStuff() {
		System.out.println("TrackCoach : inside method doMyStartupStuff");
	}
	//add an destroy method
	public void doMyCleanupStuff() {
		System.out.println("TrackCoach : inside method doMyCleanupStuff");
	}
	
}
