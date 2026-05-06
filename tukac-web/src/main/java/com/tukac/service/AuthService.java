package com.tukac.service;

import com.tukac.model.User;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Authenticate user by email/studentId and password.
     * Supports both plain-text passwords (legacy Swing app) and BCrypt.
     */
    public Optional<User> authenticate(String emailOrStudentId, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmailOrStudentId(emailOrStudentId, emailOrStudentId);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();
        String storedPassword = user.getPassword();

        boolean matches;
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            // BCrypt password
            matches = passwordEncoder.matches(rawPassword, storedPassword);
        } else {
            // Plain-text password (legacy)
            matches = storedPassword.equals(rawPassword);
        }

        return matches ? Optional.of(user) : Optional.empty();
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
    }

    public User register(String name, String studentId, String email, String rawPassword, String contact,
                         boolean hasDisability, String disabilityType, String ncpwdNumber, String passportPhoto) {
        User user = new User();
        user.setName(name);
        user.setStudentId(studentId);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setContact(contact);
        user.setRole("member");
        user.setIsApproved(0);
        user.setHasDisability(hasDisability);
        user.setDisabilityType(hasDisability ? disabilityType : null);
        user.setNcpwdNumber(hasDisability ? ncpwdNumber : null);
        user.setPassportPhoto(passportPhoto);
        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean studentIdExists(String studentId) {
        return userRepository.findByStudentId(studentId).isPresent();
    }
}
