package spring.annotattionsIoc.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IocAnnotationsDemo {
	public static void main(String args[]) {
		
		//create a spring container
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		//retrieve the bean
		Coach theCoach = context.getBean("baseballCoach",Coach.class);
		//call the method
		System.out.println(theCoach.getDailyWorkOut());
		//close the container
		context.close();
	}


}
