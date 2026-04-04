package com.zorvyn.assignment.FinancialRecordManagement;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Role;
import com.zorvyn.assignment.FinancialRecordManagement.entities.User;
import com.zorvyn.assignment.FinancialRecordManagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;


@SpringBootApplication
public class FinancialRecordManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialRecordManagementApplication.class, args);
	}


	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() == 0) {
				User admin = new User();

				admin.setUserId(UUID.randomUUID().toString());
				admin.setName("System Admin");
				admin.setEmail("system@admin.com");
				admin.setMobileNumber("9876543210");

				admin.setPassword(passwordEncoder.encode("Admin@12345"));

				admin.setRole(Role.ADMIN);
				admin.setStatus(true);
				admin.setIsDeleted(false);

				userRepository.save(admin);
				System.out.println("Seeded Admin User successfully.");
				System.out.println("Username: "+admin.getEmail());
				System.out.println("Password: Admin@12345");
			}
		};
	}
}
