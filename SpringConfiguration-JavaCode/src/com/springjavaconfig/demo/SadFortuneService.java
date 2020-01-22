package com.springjavaconfig.demo;

public class SadFortuneService implements FortuneService {

	@Override
	public String getDailyFortune() {
		return "today is a sad day";
	}

}
