package spring.setterInjection.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetterInjectionDemo {

	public static void main(String[] args) {
		//create spring container
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		//retrieve the bean
		Coach theCoach = applicationContext.getBean("tennisCoach", Coach.class);
		//calling the methods
		System.out.println(theCoach.getDailyWorkOut());
		System.out.println(theCoach.getDailyFortune());
		//close spring container
		applicationContext.close();
	}

}
