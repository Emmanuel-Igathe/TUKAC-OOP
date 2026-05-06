package com.tukac.dto;

public class AuthResponse {
    private String token;
    private String name;
    private String role;
    private Long id;
    private String studentId;
    private String email;

    public AuthResponse(String token, String name, String role, Long id, String studentId, String email) {
        this.token = token;
        this.name = name;
        this.role = role;
        this.id = id;
        this.studentId = studentId;
        this.email = email;
    }

    public String getToken() { return token; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public Long getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getEmail() { return email; }
}
