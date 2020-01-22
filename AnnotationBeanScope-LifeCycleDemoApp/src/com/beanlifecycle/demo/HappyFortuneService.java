package com.beanlifecycle.demo;

import org.springframework.stereotype.Component;

@Component
public class HappyFortuneService implements FortuneService {

	@Override
	public String getDailyFortune() {
		
		return "Today is your luckey day!!";
	}

}
