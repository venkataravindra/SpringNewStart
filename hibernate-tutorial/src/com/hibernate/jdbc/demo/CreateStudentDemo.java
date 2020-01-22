package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Student;

public class CreateStudentDemo {
	public static void main(String args[]) {
		//create session factory
		SessionFactory factory = new Configuration()
				.configure("HIBERN~1.XML")
				.addAnnotatedClass(Student.class)
				.buildSessionFactory();
		//create session
		Session session = factory.getCurrentSession();
		try {
			
			//create the student object 
			System.out.println("Creating a new student object....");
			Student tempStudent =new Student("ravindra","kumar","ravindradevarasetty@gmail.com");
			//start a transaction
			session.beginTransaction();
			//save the student object
			System.out.println("Saving the student.....");
			session.save(tempStudent);
			//commit the transaction
			session.getTransaction().commit();
			
			System.out.println("Done!");
			
		}
		finally {
			factory.close();
		}
	}

}
