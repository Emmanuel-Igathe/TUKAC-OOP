package com.tukac;

import com.tukac.model.User;
import com.tukac.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TukacApplication {
    public static void main(String[] args) {
        SpringApplication.run(TukacApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setName("System Admin");
                admin.setStudentId("ADMIN001");
                admin.setEmail("admin@tukac.com");
                admin.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin123"));
                admin.setRole("chairperson");
                admin.setIsApproved(1);
                userRepository.save(admin);
                System.out.println("Seeded default admin user (hashed): admin@tukac.com / admin123");
            } else {
                // Ensure admin@tukac.com exists in case the db was carried over but missing admin
                if (userRepository.findByEmail("admin@tukac.com").isEmpty()) {
                    User admin = new User();
                    admin.setName("System Admin");
                    admin.setStudentId("ADMIN001");
                    admin.setEmail("admin@tukac.com");
                    admin.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin123"));
                    admin.setRole("chairperson");
                    admin.setIsApproved(1);
                    userRepository.save(admin);
                    System.out.println("Seeded default admin user (hashed): admin@tukac.com / admin123");
                }
            }

            // Migrate old roles to new role names
            userRepository.findAll().forEach(u -> {
                String role = u.getRole();
                String newRole = null;
                if ("admin".equals(role))      newRole = "chairperson";
                else if ("executive".equals(role)) newRole = "vice-chairperson";
                if (newRole != null) {
                    u.setRole(newRole);
                    userRepository.save(u);
                    System.out.println("Migrated role: " + u.getEmail() + " -> " + newRole);
                }
            });
        };
    }
}
