package com.hibernate.jdbc.demo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Student;

public class QueryStudentDemo {
	public static void main(String args[]) {
		//create session factory
		SessionFactory factory = new Configuration()
				.configure("HIBERN~1.XML")
				.addAnnotatedClass(Student.class)
				.buildSessionFactory();
		//create session
		Session session = factory.getCurrentSession();
		try {
			
			//start a transaction
			session.beginTransaction();
			
			//query the students
			List<Student> theStudents = session.createQuery("from Student").list();
			//display the students
			displayStudents(theStudents);
			
			//query the students : lastName='kumar'
			theStudents = session.createQuery("from Student s where s.lastName='kumar'").list();
			
			//display the student 
			System.out.println("\nthe students who have last name of kumar");
			displayStudents(theStudents);
			
			//query students: lastName='kumar' OR firstName='pavan'
			theStudents = session.createQuery("from Student s where "
					+ "s.lastName='kumar' OR s.firstName='pavan'").list();
			//display the student 
			System.out.println("\nthe students who have last name of kumar and first name of ravindra");
			displayStudents(theStudents);
			
			//query students where email LIKE 'luv2code.com'
			theStudents = session.createQuery("from Student s where"
					 + " s.email LIKE '%luv2code.com'").list();
			//display the student 
			System.out.println("\nthe students whose email ends with 'luv2code.com'");
			displayStudents(theStudents);
			
			
			//commit the transaction
			session.getTransaction().commit();
			
			System.out.println("Done!");
			
		}
		finally {
			factory.close();
		}
	}

	private static void displayStudents(List<Student> theStudents) {
		for(Student tempStudent : theStudents) {
			System.out.println(tempStudent);
		}
	}

}
