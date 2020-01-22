package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Course;
import com.hibernate.jdbc.entity.Instructor;
import com.hibernate.jdbc.entity.InstructorDetail;
import com.hibernate.jdbc.entity.Student;

public class DeleteCourseDemo {
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
			//get a course
			
			int theId = 10;
			Course tempCourse = session.get(Course.class, theId);
			
			//delete a course
			System.out.println("Deleting Course :" +tempCourse);
			session.delete(tempCourse);
			//commit the transaction
			session.getTransaction().commit();
			
			System.out.println("Done!");
			
		}
		finally {
			session.close();
			factory.close();
		}
	}

}
