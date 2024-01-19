package com.dc;

import java.io.Serializable;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String department;

    public Employee(String username, String department) {
        this.username = username;
        this.department = department;
    }

    public String getUsername() {
        return username;
    }

    public String getDepartment() {
        return department;
    }
}
