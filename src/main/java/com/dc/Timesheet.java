package com.dc;
import java.io.Serializable;
import java.time.LocalDate;

public class Timesheet implements Serializable {
    private static final long serialVersionUID = 1L;

    private Employee employee;
    private LocalDate weekStartDate;
    private int[] hoursWorked;
    private boolean submitted;
    private boolean approved;

    public Timesheet(Employee employee, LocalDate weekStartDate, int[] hoursWorked) {
        this.employee = employee;
        this.weekStartDate = weekStartDate;
        this.hoursWorked = hoursWorked;
        this.submitted = submitted;
        this.approved = approved;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public int[] getHoursWorked() {
        return hoursWorked;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public boolean isApproved() {
        return approved;
    }

    public void submit() {
        this.submitted = true;
    }

    public void approve() {
        this.approved = true;
    }
}
