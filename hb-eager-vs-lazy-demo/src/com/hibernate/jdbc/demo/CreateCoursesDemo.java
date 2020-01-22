package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Course;
import com.hibernate.jdbc.entity.Instructor;
import com.hibernate.jdbc.entity.InstructorDetail;
import com.hibernate.jdbc.entity.Student;

public class CreateCoursesDemo {
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
			//create some courses
			Course tempCourse1 = new Course("Programming through C-ITE101");
			Course tempCourse2 = new Course("Programming through C++-SWE101");
			Course tempCourse3 = new Course("Programming through java-SWE204");
			Course tempCourse4 = new Course("Programming through java lab-SWE205");
			

			//add courses to instructor
			tempInstructor.add(tempCourse1);
			tempInstructor.add(tempCourse2);
			tempInstructor.add(tempCourse3);
			tempInstructor.add(tempCourse4);
			
			//save courses
			session.save(tempCourse1);
			session.save(tempCourse2);
			session.save(tempCourse3);
			session.save(tempCourse4);
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
