package com.springjavaconfig.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SwimJavaConfigDemoApp {
	public static void main(String args[]) {
		//read spring configuration class
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpotConfig.class);
		//retrieve bean from container
		SwimCoach theCoach = context.getBean("swimCoach",SwimCoach.class);
		
		//calling methods through theCoach
		System.out.println(theCoach.getDailyFortune());
		System.out.println(theCoach.getDailyWorkOut());
		//calling our new swim coach methods....has the properties values injected
		System.out.println("email :"+theCoach.getEmail());
		System.out.println("team :"+theCoach.getTeam());
		//closing context
		context.close();
	}

}
