package Mainclasses;   

import java.io.Serializable;

public abstract class User implements Serializable {

    private String id;          
    private String name;
    private String email;
    private String password;  
    private boolean gender;
    private int phoneNumber;


    protected User() {
    }

    protected User(String id, String name, String email, String password,
                   boolean gender, int phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

   
    public boolean checkPassword(String rawPassword) {
        return this.password != null && this.password.equals(rawPassword);
    }

    public void updateProfile(String name, String email, String password, int phoneNumber) {
        this.name = name;
        this.email = email;
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        this.phoneNumber = phoneNumber;
    }

    // ----- Getters & setters -----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
