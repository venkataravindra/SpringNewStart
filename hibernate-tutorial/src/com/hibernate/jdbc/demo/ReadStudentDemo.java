package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Student;

public class ReadStudentDemo {
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
			Student tempStudent =new Student("pavan","kumar","pavankumar@gmail.com");
			//start a transaction
			session.beginTransaction();
			//save the student object
			System.out.println("Saving the student.....");
			System.out.println(tempStudent);
			session.save(tempStudent);
			//commit the transaction
			session.getTransaction().commit();
			//My new Code
			
			//find out student's id: primary key
			System.out.println("Saved Student. Generated id:" +tempStudent);
			//now get a new session ans start new session
			session = factory.getCurrentSession();
			session.beginTransaction();
			//retrieve student based on the id: primary key
			System.out.println("\n Getting Student with id:" +tempStudent.getId());
			Student myStudent = session.get(Student.class, tempStudent.getId());
			System.out.println("Get Complete:" +myStudent); 
			//commit the transaction 
			session.getTransaction().commit();
			System.out.println("Done!");
			
		}
		finally {
			factory.close();
		}
	}

}
