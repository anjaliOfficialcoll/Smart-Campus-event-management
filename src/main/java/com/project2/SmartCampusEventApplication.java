package com.project2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartCampusEventApplication {


    public static void main(String[] args) {
        SpringApplication.run(SmartCampusEventApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Smart Campus Events App is running!");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("  Admin Login: http://localhost:8080/admin/login");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }

}
