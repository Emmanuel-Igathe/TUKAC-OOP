package com.tukac.dto;

public class LoginRequest {
    private String emailOrStudentId;
    private String password;

    public LoginRequest() {}

    public String getEmailOrStudentId() { return emailOrStudentId; }
    public void setEmailOrStudentId(String emailOrStudentId) { this.emailOrStudentId = emailOrStudentId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
