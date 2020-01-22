package com.setterinjection.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Springsetterinjection {

	public static void main(String[] args) {
		// load the spring configuration file
				ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
				// retrieve the bean from container
				CricketCoach theCoach = context.getBean("mycricketCoach", CricketCoach.class);
				// call methods from bean
				System.out.println(theCoach.getDailyWorkOut());
				//let's call new method for fortunes
				System.out.println(theCoach.getDailyFortune());
				//call new methods to get the literal values
				System.out.println(theCoach.getEmailAddress());
				System.out.println(theCoach.getTeam());
				// close the context
				context.close();

	}

}
