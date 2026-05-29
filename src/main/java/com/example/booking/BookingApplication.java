package com.example.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class that starts our Spring Boot application.
 */
@SpringBootApplication
public class BookingApplication {

    public static void main(String[] args) {
        // This line runs the Tomcat web server and starts the application
        SpringApplication.run(BookingApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  Global Class Booking System is RUNNING!  ");
        System.out.println("  Access API endpoints at http://localhost:8080   ");
        System.out.println("=================================================");
    }
}
