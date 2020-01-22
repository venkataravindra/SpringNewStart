package com.beanscope.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;



public class BeanScope {
	public static void main(String args[]) {
		// load the spring configuration file
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beanScope-applicationContext.xml");
		// retrieve the bean from container
		Coach theCoach = context.getBean("myCoach", Coach.class);
		
		Coach alphaCoach = context.getBean("myCoach", Coach.class);
		
	//check the equality of beans	
	  boolean result = (theCoach == alphaCoach);
	//print the results  
	  System.out.println("\n Pointing to the same object   :"+result);
	  System.out.println("\n Memory location for theCoach   :" +theCoach);
	  System.out.println("\n Memory location for alphaCoach :" +alphaCoach+ "\n");

		context.close();
		
	}

}
