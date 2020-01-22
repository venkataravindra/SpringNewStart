package com.dependencyinjection.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Springdependencyinjection {

	public static void main(String[] args) {
	
		// load the spring configuration file
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// retrieve the bean from container
		Coach theCoach = context.getBean("myCoach", Coach.class);
		// call methods from bean
		System.out.println(theCoach.getDailyWorkOut());
		//let's call new method for fortunes
		System.out.println(theCoach.getDailyFortune());
		// close the context
		context.close();

	}

}
