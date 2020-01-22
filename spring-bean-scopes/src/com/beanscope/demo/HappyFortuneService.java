package com.beanscope.demo;

public class HappyFortuneService implements FortuneService {

	@Override
	public String getFortune() {
		return "Today is your luckey day!";
	}

}
