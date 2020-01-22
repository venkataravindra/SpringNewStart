package com.hibernate.jdbc.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.hibernate.jdbc.entity.Instructor;
import com.hibernate.jdbc.entity.InstructorDetail;
import com.hibernate.jdbc.entity.Student;

public class DeleteDemo {
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
			//get instructor by primary key 
			
			//delete the instructors
			
			int theId=2;
			Instructor tempInstrucor = session.get(Instructor.class, theId);
			
			System.out.println("Found instructor: "+tempInstrucor);
			//delete instructor
			if(tempInstrucor != null) {
				System.out.println("Deleting Instructor "+tempInstrucor);
				//Note it will also delete the instructorDetail 
				//because of CascadeType.ALL
				session.delete(tempInstrucor);
			}
			//commit the transaction
			session.getTransaction().commit();
			
			System.out.println("Done!");
			
		}
		finally {
			factory.close();
		}
	}

}
