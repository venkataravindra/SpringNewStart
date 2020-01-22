package spring.setterInjection.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class TennisCoach implements Coach {

	private FortuneService fortuneService;
	public TennisCoach() {
		System.out.println(">> inside TennisCoach Constructor");
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
