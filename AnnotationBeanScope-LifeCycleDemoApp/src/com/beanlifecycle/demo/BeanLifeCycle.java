package com.beanlifecycle.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanLifeCycle {
	public static void main(String args[]) {
		//create application container
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		//retrieve bean from container
		Coach theCoach = context.getBean("tennisCoach",Coach.class);
		
		Coach alphaCoach = context.getBean("tennisCoach",Coach.class);
		//calling methods through theCoach
		System.out.println(theCoach.getDailyFortune());
		System.out.println(theCoach.getDailyWorkOut());
		//calling methods through alphaCoach
		System.out.println(alphaCoach.getDailyFortune());
		System.out.println(alphaCoach.getDailyWorkOut());
		//check if both beans are same
		boolean result = (theCoach == alphaCoach);
		System.out.println("\n pointing to the same object :"+result);
		System.out.println("\n Memory Location for theCoach :"+theCoach);
		System.out.println("\n Memory Location for alphaCoach :"+alphaCoach);
		
		//closing context
		context.close();
	}

}
