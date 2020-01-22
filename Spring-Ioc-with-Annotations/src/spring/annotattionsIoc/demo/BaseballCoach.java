package spring.annotattionsIoc.demo;

import org.springframework.stereotype.Component;

@Component
public class BaseballCoach implements Coach {

	@Override
	public String getDailyWorkOut() {
		
		return "Spend 30 minutes on batting practise";
	}

}
