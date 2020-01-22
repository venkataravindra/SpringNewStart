package com.springjavaconfig.demo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TennisCoach implements Coach {

	private FortuneService fortuneService;
	public TennisCoach() {
		System.out.println(">>TennisCoach: inside TennisCoach Constructor");
	}
	//define init method
	@PostConstruct
	public void doMyStartUpStuff() {
		System.out.println(">>TennisCoach: inside doMyStartUpStuff()");
	}
	//define destroy method
	@PreDestroy
	public void doMyCleanUpStuff() {
		System.out.println(">>TennisCoach: inside doMyCleanUpStuff()");
	}
	
	@Autowired
	public void setFortuneService(FortuneService fortuneService) {
		System.out.println(">> inside setFortuneService method");
		this.fortuneService = fortuneService;
	}
	@Override
	public String getDailyWorkOut() {
		
		return "practise yur backhand valley";
	}

	@Override
	public String getDailyFortune() {
		
		return fortuneService.getDailyFortune();
	}


}
