package com.tukac.service;

import com.tukac.model.User;
import com.tukac.repository.ActivityLogRepository;
import com.tukac.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private HttpServletRequest request;

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

        if (matches) {
            logActivity(user.getId(), user.getName(), "LOGIN", "User logged in via Web Portal");
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public void logActivity(Long userId, String userName, String action, String details) {
        String ip = request.getRemoteAddr();
        com.tukac.model.ActivityLog log = new com.tukac.model.ActivityLog(userId, userName, action, details, ip);
        activityLogRepository.save(log);
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
    }

    public Optional<User> authenticateReset(String email, String studentId) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getStudentId().equals(studentId)) {
            return userOpt;
        }
        return Optional.empty();
    }

    public void resetPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logActivity(user.getId(), user.getName(), "PASSWORD_RESET", "Password was reset by the user");
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
        User saved = userRepository.save(user);
        logActivity(saved.getId(), saved.getName(), "REGISTER", "New user registered: " + saved.getEmail());
        return saved;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean studentIdExists(String studentId) {
        return userRepository.findByStudentId(studentId).isPresent();
    }
}
