package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.hibernate.jdbc.entity.Course;
import com.hibernate.jdbc.entity.Instructor;
import com.hibernate.jdbc.entity.InstructorDetail;

public class FetchJoinDemo {
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
			//option 2: Hibernate query HQL
			
			//get the instructor from db
			int theId=1;
			Query<Instructor> query = session.createQuery("select i from Instructor i "
											           +"JOIN FETCH i.courses"
											           + " where i.id=:theInstructorId",Instructor.class);
			//set parameter on query
			query.setParameter("theInstructorId", theId);
			
			//execute query and get Instructor
			Instructor tempInstructor = query.getSingleResult();
System.out.println("Ravindra: Instructor: " + tempInstructor);	
			
			// commit transaction
			session.getTransaction().commit();
			
			// close the session
			session.close();
			
			System.out.println("Ravindra: The session is now closed!\n");
			
			// get courses for the instructor
			System.out.println("Ravindra: Courses: " + tempInstructor.getCourses());
			
			System.out.println("Ravindra: Done!");
			
		}
		finally {
			session.close();
			factory.close();
		}
	}

}
