package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Course;
import com.hibernate.jdbc.entity.Instructor;
import com.hibernate.jdbc.entity.InstructorDetail;
import com.hibernate.jdbc.entity.Student;

public class CreateInstructorDemo {
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

			//create the objects
			Instructor tempInstructor = new Instructor("ravindra","kumar","ravindradevarasetty@gmail.com");
			InstructorDetail tempInstructorDetail = new InstructorDetail("http://www.youtube.com/ravindra","listening music");
			
			//associate the objects
			tempInstructor.setInstructorDetail(tempInstructorDetail);
			//start a transaction
			session.beginTransaction();
			//save the instructor
			//Note: This also saves the detail object also
			//because of Cascade.ALL
			System.out.println("Saving Instructor:" +tempInstructor);
			session.save(tempInstructor);
			
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
