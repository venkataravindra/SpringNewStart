package com.beanscope.demo;

public class CricketCoach implements Coach {

	private FortuneService fortuneService;
	private String emailAddress;
	private String team;
	//create a no-arg constructor
	public CricketCoach() {
		System.out.println("CricketCoach : inside no-arg constructor");
	}
	
	
	public String getEmailAddress() {
		
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		System.out.println("CricketCoach : inside setEmailAddress");
		this.emailAddress = emailAddress;
	}


	public String getTeam() {
		return team;
	}


	public void setTeam(String team) {
		System.out.println("CricketCoach : inside setTeam");
		this.team = team;
	}


	//our setter method
	public void setFortuneService(FortuneService fortuneService) {
		System.out.println("CricketCoach : inside setter method - setFortuneService ");
		this.fortuneService = fortuneService;
	}


	@Override
	public String getDailyWorkOut() {
		
		return "Practice fast bowling for 15 minutes";
	}

	@Override
	public String getDailyFortune() {
		// TODO Auto-generated method stub
		return fortuneService.getFortune();
	}

}
