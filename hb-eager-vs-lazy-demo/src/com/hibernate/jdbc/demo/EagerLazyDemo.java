package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Course;
import com.hibernate.jdbc.entity.Instructor;
import com.hibernate.jdbc.entity.InstructorDetail;
import com.hibernate.jdbc.entity.Student;

public class EagerLazyDemo {
	public static void main(String args[]) {
		//create session factory
		SessionFactory factory = new Configuration()
				.configure("HIBERN~1.XML")
				.addAnnotatedClass(Instructor.class)
				.addAnnotatedClass(InstructorDetail.class)
				.addAnnotatedClass(Course.class)
				.buildSessionFactory();
		//create session
		Session session = factory.getCurrentSession();
		try {
			//start a transaction
			session.beginTransaction();
			//get the instructor from db
			int theId=1;
			Instructor tempInstructor = session.get(Instructor.class, theId);
			System.out.println("Ravindra: Instructor : " +tempInstructor);
			
			//Get Course for the Instructor
			System.out.println("Ravindra: Courses :" +tempInstructor.getCourses());
			
			//commit the transaction
			session.getTransaction().commit();
			
			System.out.println("Ravindra: Done!");
			
		}
		finally {
			session.close();
			factory.close();
		}
	}

}
