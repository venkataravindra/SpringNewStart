package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Instructor;
import com.hibernate.jdbc.entity.InstructorDetail;
import com.hibernate.jdbc.entity.Student;

public class GetInstructorDetailDemo {
	public static void main(String args[]) {
		//create session factory
		SessionFactory factory = new Configuration()
				.configure("HIBERN~1.XML")
				.addAnnotatedClass(Instructor.class)
				.addAnnotatedClass(InstructorDetail.class)
				.buildSessionFactory();
		//create session
		Session session = factory.getCurrentSession();
		try {

			
			//start a transaction
			session.beginTransaction();
			//get the instructor detail object
			int theId=3999; 
			InstructorDetail tempInstructorDetail = session.get(InstructorDetail.class, theId);
			//print the instruct detail object
			System.out.println("tempinstructorDetail" +tempInstructorDetail);
			//print the associated instructor 
			System.out.println("the associated instructor:" +tempInstructorDetail.getInstructor());
		
		
		
			//commit the transaction
			session.getTransaction().commit();
			
			System.out.println("Done!");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			session.close();

			factory.close();
		}
	}

}
