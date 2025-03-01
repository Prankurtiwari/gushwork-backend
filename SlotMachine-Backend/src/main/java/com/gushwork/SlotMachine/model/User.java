package com.gushwork.SlotMachine.model;


import jakarta.validation.constraints.NotEmpty;

public class User {
    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Email is required")
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
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
}
