package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Student;

public class UpdateStudentDemo {
	public static void main(String args[]) {
		//create session factory
		SessionFactory factory = new Configuration()
				.configure("HIBERN~1.XML")
				.addAnnotatedClass(Student.class)
				.buildSessionFactory();
		//create session
		Session session = factory.getCurrentSession();
		try {
			int studentId =1;
			
			//start a transaction
			session.beginTransaction();
			//commit the transaction
			session.getTransaction().commit();
			//My new Code
			
			//now get a new session ans start new session
			session = factory.getCurrentSession();
			session.beginTransaction();
			//retrieve student based on the id: primary key
			System.out.println("\n Getting Student with id:" +studentId);
			Student myStudent = session.get(Student.class, studentId);
			System.out.println("Updating Student....");
			myStudent.setFirstName("venkata ravindra");
			//commit the transaction 
			session.getTransaction().commit();
			System.out.println("Done!");
			
		}
		finally {
			factory.close();
		}
	}

}
