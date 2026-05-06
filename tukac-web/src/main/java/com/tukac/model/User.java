package com.tukac.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "student_id", unique = true, nullable = false)
    private String studentId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "member";

    @Column(name = "is_approved")
    private int isApproved = 0;

    private String contact;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── New disability fields ──────────────────────────────────────
    @Column(name = "has_disability")
    private Boolean hasDisability = false;

    @Column(name = "disability_type")
    private String disabilityType;

    @Column(name = "ncpwd_number")
    private String ncpwdNumber;

    // ── Passport photo (base64 data URL) ──────────────────────────
    @Column(name = "passport_photo", columnDefinition = "TEXT")
    private String passportPhoto;

    public User() {}

    public User(String name, String studentId, String email, String password) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.password = password;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getIsApproved() { return isApproved; }
    public void setIsApproved(int isApproved) { this.isApproved = isApproved; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean isHasDisability() { return hasDisability != null && hasDisability; }
    public void setHasDisability(Boolean hasDisability) { this.hasDisability = hasDisability != null && hasDisability; }

    public String getDisabilityType() { return disabilityType; }
    public void setDisabilityType(String disabilityType) { this.disabilityType = disabilityType; }

    public String getNcpwdNumber() { return ncpwdNumber; }
    public void setNcpwdNumber(String ncpwdNumber) { this.ncpwdNumber = ncpwdNumber; }

    public String getPassportPhoto() { return passportPhoto; }
    public void setPassportPhoto(String passportPhoto) { this.passportPhoto = passportPhoto; }

    public boolean isAdmin() { return "chairperson".equals(role); }
    public boolean isExecutive() { return "vice-chairperson".equals(role) || "secretary".equals(role) || "treasurer".equals(role) || isAdmin(); }
}
