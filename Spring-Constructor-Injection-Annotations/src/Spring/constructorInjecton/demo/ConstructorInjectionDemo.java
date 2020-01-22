package Spring.constructorInjecton.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConstructorInjectionDemo {
	public static void main(String args[]) {
		//create bean container
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		//get the bean
		Coach theCoach = applicationContext.getBean("tennisCoach" , Coach.class);
		//calling methods using bean
		System.out.println(theCoach.getDailyWorkOut());
		System.out.println(theCoach.getDailyFortune());
		//close bean container
		applicationContext.close();
	}

}
