package com.example.myapp;

public class Contact {
    private String name;
    private String phone;

    // Constructor to initialize the Contact object
    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // Getter method for the phone number
    public String getPhone() {
        return phone;
    }

    // Getter method for the name (optional, but good practice)
    public String getName() {
        return name;
    }

    // Override toString() to display the contact's name in the ListView
    @Override
    public String toString() {
        return name;
    }
}
