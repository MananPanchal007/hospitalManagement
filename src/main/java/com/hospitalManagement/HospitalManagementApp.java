package com.hospitalManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Hospital Management System
 * 
 * This Spring Boot application provides RESTful APIs for managing:
 * - Patient records and medical history
 * - Doctor profiles and availability
 * - Appointment booking and cancellation
 * 
 * Features:
 * - Transactional operations with automatic rollback
 * - Pessimistic locking for concurrency control
 * - Bean Validation for input validation
 * - Swagger UI for API documentation
 * - Spring Security for API protection
 * 
 * @author Manan Panchal
 * @version 0.0.1
 */
@SpringBootApplication
public class HospitalManagementApp {

	/**
	 * Application entry point
	 * Starts the Spring Boot application and initializes all components
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HospitalManagementApp.class, args);
	}

}
