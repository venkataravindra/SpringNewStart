package Spring.constructorInjecton.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TennisCoach implements Coach {

	private FortuneService fortuneService;
	
	@Autowired
	public TennisCoach(FortuneService fortuneService) {
		super();
		this.fortuneService = fortuneService;
	}


	@Override
	public String getDailyWorkOut() {
		
		return "practise your backhand valley";
	}


	@Override
	public String getDailyFortune() {
		
		return fortuneService.getDailyFortune();
	}
	

}
