package com.tukac;

import com.tukac.model.*;
import com.tukac.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class TukacApplication {

    public static void main(String[] args) {
        SpringApplication.run(TukacApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(
            UserRepository userRepo,
            EventRepository eventRepo,
            BlogPostRepository blogRepo,
            TransactionRepository transRepo,
            EventRegistrationRepository regRepo,
            PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() > 0) return;

            // Seed Users
            User admin = new User();
            admin.setName("Admin User"); admin.setStudentId("ADM001");
            admin.setEmail("admin@tukac.ac.ke"); admin.setPassword(encoder.encode("admin123"));
            admin.setRole("ADMIN"); admin.setApprovalStatus("APPROVED");
            admin.setContactDetails("0712345678"); userRepo.save(admin);

            User exec = new User();
            exec.setName("Jane Wanjiku"); exec.setStudentId("EXE001");
            exec.setEmail("exec@tukac.ac.ke"); exec.setPassword(encoder.encode("exec123"));
            exec.setRole("EXECUTIVE"); exec.setApprovalStatus("APPROVED");
            exec.setContactDetails("0723456789"); userRepo.save(exec);

            User member = new User();
            member.setName("John Kamau"); member.setStudentId("MEM001");
            member.setEmail("member@tukac.ac.ke"); member.setPassword(encoder.encode("member123"));
            member.setRole("MEMBER"); member.setApprovalStatus("APPROVED");
            member.setContactDetails("0734567890"); userRepo.save(member);

            User pending = new User();
            pending.setName("Alice Muthoni"); pending.setStudentId("MEM002");
            pending.setEmail("pending@tukac.ac.ke"); pending.setPassword(encoder.encode("pending123"));
            pending.setRole("MEMBER"); pending.setApprovalStatus("PENDING");
            pending.setContactDetails("0745678901"); userRepo.save(pending);

            // Seed Events
            Event ev1 = new Event();
            ev1.setTitle("Disability Awareness Week 2025");
            ev1.setDescription("Annual event to raise awareness about disability rights and inclusion at TUK. Join us for inspiring talks, art exhibitions, and community-building activities that celebrate every ability.");
            ev1.setDate(LocalDate.now().plusDays(14)); ev1.setTime("09:00 AM");
            ev1.setLocation("TUK Main Hall, Block A"); ev1.setCapacity(200);
            ev1.setCreatedBy(admin); eventRepo.save(ev1);

            Event ev2 = new Event();
            ev2.setTitle("Kenyan Sign Language Workshop");
            ev2.setDescription("Learn basic Kenyan Sign Language (KSL) in this interactive, hands-on workshop open to all students and staff. No prior knowledge needed!");
            ev2.setDate(LocalDate.now().plusDays(7)); ev2.setTime("02:00 PM");
            ev2.setLocation("Room B204, TUK"); ev2.setCapacity(50);
            ev2.setCreatedBy(exec); eventRepo.save(ev2);

            Event ev3 = new Event();
            ev3.setTitle("Inclusive Sports Day");
            ev3.setDescription("A fun day of adapted sports and games for everyone. All abilities are welcome! Come enjoy wheelchair basketball, blind football, and more.");
            ev3.setDate(LocalDate.now().plusDays(21)); ev3.setTime("10:00 AM");
            ev3.setLocation("TUK Sports Grounds"); ev3.setCapacity(150);
            ev3.setCreatedBy(admin); eventRepo.save(ev3);

            Event ev4 = new Event();
            ev4.setTitle("Mental Health & Wellness Seminar");
            ev4.setDescription("A candid conversation about mental health challenges faced by persons with disabilities in university settings, with licensed counselors and peer support.");
            ev4.setDate(LocalDate.now().minusDays(5)); ev4.setTime("11:00 AM");
            ev4.setLocation("Auditorium, Block C"); ev4.setCapacity(100);
            ev4.setCreatedBy(exec); eventRepo.save(ev4);

            // Seed Event Registrations
            EventRegistration reg1 = new EventRegistration();
            reg1.setEvent(ev1); reg1.setUser(member); reg1.setRegisteredAt(LocalDateTime.now());
            regRepo.save(reg1);
            EventRegistration reg2 = new EventRegistration();
            reg2.setEvent(ev2); reg2.setUser(member); reg2.setRegisteredAt(LocalDateTime.now());
            regRepo.save(reg2);

            // Seed Blog Posts
            BlogPost bp1 = new BlogPost();
            bp1.setTitle("Understanding Disability Rights in Kenya");
            bp1.setContent("Kenya has made significant strides in disability rights through the Persons with Disabilities Act 2003 and the Constitution 2010, which explicitly recognizes the rights of persons with disabilities. Article 54 of the Constitution guarantees the right to access educational institutions, reasonable accommodation, and assistive devices.\n\nAt TUKAC, we continue to advocate for the full implementation of these rights within our university. Our club works tirelessly to ensure that every student, regardless of ability, can fully participate in campus life.\n\nKey milestones in Kenya's disability rights journey include the establishment of the National Council for Persons with Disabilities (NCPWD) and the ratification of the UN Convention on the Rights of Persons with Disabilities (CRPD) in 2008.\n\nWe encourage all members to familiarize themselves with these rights and to speak up when they see violations.");
            bp1.setAuthor(admin); bp1.setCategory("Advocacy");
            blogRepo.save(bp1);

            BlogPost bp2 = new BlogPost();
            bp2.setTitle("Sign Language: Bridging the Communication Gap");
            bp2.setContent("Kenyan Sign Language (KSL) is the primary language of the Deaf community in Kenya, with an estimated 600,000 Deaf individuals relying on it daily. Despite this, KSL remains largely unknown to the hearing population, creating significant communication barriers in education, healthcare, and employment.\n\nLearning even basic signs can dramatically improve inclusion and communication across our campus. At TUKAC, we run monthly KSL workshops open to all students and staff. Our volunteer instructors from the Deaf community bring authentic knowledge and lived experience to every session.\n\nSome basic signs to get you started: Hello (wave your hand near your face), Thank you (touch your chin and move forward), Help (fist thumb up, place on open palm), Friend (interlock index fingers).\n\nJoin our next workshop and take the first step toward a more inclusive campus!");
            bp2.setAuthor(exec); bp2.setCategory("Education");
            blogRepo.save(bp2);

            BlogPost bp3 = new BlogPost();
            bp3.setTitle("Making TUK Campus More Accessible");
            bp3.setContent("Technical University of Kenya has been making steady progress in campus accessibility, but there is still work to be done. This article reviews the current state and what still needs improvement.\n\nPositive developments include new ramps at the Library entrance, accessible restrooms in Block A and Block B, tactile paving along the main walkway, and reserved parking spaces near key buildings.\n\nAreas needing improvement: the lecture halls in Block D still lack proper ramp access, the student cafeteria does not have accessible seating arrangements, and many doors require two hands to open making them difficult for wheelchair users.\n\nTUKAC has submitted a formal accessibility audit report to the university administration. We are optimistic that these issues will be addressed in the upcoming infrastructure upgrades. Together, we can make TUK a truly inclusive campus for all.");
            bp3.setAuthor(member); bp3.setCategory("Campus Life");
            blogRepo.save(bp3);

            // Seed Transactions
            Transaction t1 = new Transaction();
            t1.setDescription("Annual Membership Dues Collection");
            t1.setAmount(new BigDecimal("15000.00")); t1.setType("INCOME");
            t1.setCategory("Membership"); t1.setDate(LocalDate.now().minusDays(30));
            t1.setCreatedBy(admin); transRepo.save(t1);

            Transaction t2 = new Transaction();
            t2.setDescription("Event Equipment Purchase - Microphones & Projectors");
            t2.setAmount(new BigDecimal("8500.00")); t2.setType("EXPENSE");
            t2.setCategory("Equipment"); t2.setDate(LocalDate.now().minusDays(20));
            t2.setCreatedBy(exec); transRepo.save(t2);

            Transaction t3 = new Transaction();
            t3.setDescription("Government Grant - Disability Support Fund 2025");
            t3.setAmount(new BigDecimal("50000.00")); t3.setType("INCOME");
            t3.setCategory("Grant"); t3.setDate(LocalDate.now().minusDays(10));
            t3.setCreatedBy(admin); transRepo.save(t3);

            Transaction t4 = new Transaction();
            t4.setDescription("KSL Workshop Materials & Facilitator Fee");
            t4.setAmount(new BigDecimal("3200.00")); t4.setType("EXPENSE");
            t4.setCategory("Events"); t4.setDate(LocalDate.now().minusDays(5));
            t4.setCreatedBy(exec); transRepo.save(t4);

            Transaction t5 = new Transaction();
            t5.setDescription("Fundraising Dinner - Disability Awareness");
            t5.setAmount(new BigDecimal("22000.00")); t5.setType("INCOME");
            t5.setCategory("Fundraising"); t5.setDate(LocalDate.now().minusDays(2));
            t5.setCreatedBy(admin); transRepo.save(t5);

            System.out.println("==================================================");
            System.out.println("  ✅ TUKAC Application Started Successfully!");
            System.out.println("  🌐 Visit: http://localhost:8080");
            System.out.println("  👤 Login: admin@tukac.ac.ke / admin123");
            System.out.println("  👤 Login: exec@tukac.ac.ke / exec123");
            System.out.println("  👤 Login: member@tukac.ac.ke / member123");
            System.out.println("==================================================");
        };
    }
}
