package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Student;

public class PrimaryKeyDemo {

	public static void main(String[] args) {
		//create session factory
				SessionFactory factory = new Configuration()
						.configure("HIBERN~1.XML")
						.addAnnotatedClass(Student.class)
						.buildSessionFactory();
				//create session
				Session session = factory.getCurrentSession();
				try {
					
					//create student 3 student objects 
					System.out.println("Creating a new student object....");
					Student tempStudent1 =new Student("eswar","prasad","epc001@gmail.com");
					Student tempStudent2 =new Student("lakshman","sirish","lakshmanbandlamudi@gmail.com");
					Student tempStudent3 =new Student("jithendra","reddy","jithendraprabha@gmail.com");
					
					//start a transaction
					session.beginTransaction();
					//save the student object
					System.out.println("Saving the 3 students.....");
					session.save(tempStudent1);
					session.save(tempStudent2);
					session.save(tempStudent3);
					//commit the transaction
					session.getTransaction().commit();
					
					System.out.println("Done!");
					
				}
				finally {
					factory.close();
				}

	}

}
