package Spring.constructorInjecton.demo;

import org.springframework.stereotype.Component;

@Component
public class HappyFortuneService implements FortuneService {

	@Override
	public String getDailyFortune() {
		
		return "Today your luckey day!!";
	}

}
