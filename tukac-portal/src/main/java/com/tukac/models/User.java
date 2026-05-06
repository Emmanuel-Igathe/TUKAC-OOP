package com.tukac.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column
    private String studentId;

    @Column
    private String phone;

    @Column
    private String bio;

    @Column
    private String role;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    // Legacy constructor for backward compatibility with Swing UI
    public User(int id, String name, String studentId, String email, String role) {
        this.id = (long) id;
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.role = role;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
        if (role == null) {
            role = "member";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isExecutive() {
        return "executive".equals(role) || isAdmin();
    }

    public boolean isMember() {
        return "member".equals(role) || isExecutive();
    }
}
