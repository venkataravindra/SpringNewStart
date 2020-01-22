package com.beanscope.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;



public class BeanLifeCycle {
	public static void main(String args[]) {
		// load the spring configuration file
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beanLifeCycle-applicationContext.xml");
		// retrieve the bean from container
		Coach theCoach = context.getBean("myCoach", Coach.class);
		System.out.println(theCoach.getDailyWorkOut());
		context.close();
		
	}

}
