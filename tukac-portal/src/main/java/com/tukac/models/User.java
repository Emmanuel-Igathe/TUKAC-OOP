package com.tukac.models;

public class User {
    private Long id;
    private String name;
    private String studentId;
    private String email;
    private String role;
    private int isApproved;
    private String contact;
    private String createdAt;
    private boolean hasDisability;
    private String disabilityType;
    private String ncpwdNumber;
    private String passportPhoto;

    public User(Long id, String name, String studentId, String email, String role) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.role = role;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public int getIsApproved() { return isApproved; }
    public String getContact() { return contact; }
    public String getCreatedAt() { return createdAt; }
    public boolean isHasDisability() { return hasDisability; }
    public String getDisabilityType() { return disabilityType; }
    public String getNcpwdNumber() { return ncpwdNumber; }
    public String getPassportPhoto() { return passportPhoto; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setIsApproved(int isApproved) { this.isApproved = isApproved; }
    public void setContact(String contact) { this.contact = contact; }
    public void setHasDisability(boolean hasDisability) { this.hasDisability = hasDisability; }
    public void setDisabilityType(String disabilityType) { this.disabilityType = disabilityType; }
    public void setNcpwdNumber(String ncpwdNumber) { this.ncpwdNumber = ncpwdNumber; }
    public void setPassportPhoto(String passportPhoto) { this.passportPhoto = passportPhoto; }

    public boolean isAdmin() { return "chairperson".equals(role); }
    public boolean isExecutive() { 
        return "vice-chairperson".equals(role) || "secretary".equals(role) || "treasurer".equals(role) || isAdmin(); 
    }
    public boolean isMember() { return "member".equals(role); }
}