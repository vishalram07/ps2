package com.dc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimeExpressionApp extends Application {
    private static final String EMPLOYEES_FILE = "employees.csv";
    private static final String TIMESHEETS_FILE = "timesheets.csv";

    private List<Employee> employees = Arrays.asList(
        new Employee("John", null),
        new Employee("Jane", null),
        new Employee("Mike", null)
    );
    private List<Timesheet> timesheets;

    private ListView<Timesheet> timesheetListView;

    public TimeExpressionApp() {
        this.employees = loadEmployees();
        this.timesheets = loadTimesheets();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Time Expression App");

        timesheetListView = new ListView<>();
        updateTimesheetListView();

        Button submitButton = new Button("Submit Timesheet");
        submitButton.setOnAction(e -> showSubmitTimesheetDialog());

        Button approveButton = new Button("Approve Timesheet");
        approveButton.setOnAction(e -> approveSelectedTimesheet());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(timesheetListView, submitButton, approveButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void updateTimesheetListView() {
        timesheetListView.getItems().setAll(timesheets);
    }

    private void showSubmitTimesheetDialog() {
        Dialog<Timesheet> dialog = new Dialog<>();
        dialog.setTitle("Submit Timesheet");

        Label usernameLabel = new Label("Username:");
        TextField usernameTextField = new TextField();

        Label hoursLabel = new Label("Hours Worked (comma-separated):");
        TextField hoursTextField = new TextField();

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(new VBox(10, usernameLabel, usernameTextField, hoursLabel, hoursTextField));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return submitTimesheet(usernameTextField.getText(), hoursTextField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result != null) {
                timesheets.add(result);
                saveTimesheets();
                updateTimesheetListView();
            }
        });
    }

    private Timesheet submitTimesheet(String username, String hoursStr) {
    Employee employee = findEmployeeByUsername(username);
    
    if (employee == null) {
        showAlert("Error", "Employee not found for username: " + username);
        return null;
    }

    try {
        String[] hoursArray = hoursStr.split(",");
        int[] hoursWorked = new int[hoursArray.length];
        for (int i = 0; i < hoursArray.length; i++) {
            hoursWorked[i] = Integer.parseInt(hoursArray[i].trim());
        }

        Timesheet timesheet = new Timesheet(employee, LocalDate.now(), hoursWorked);
        timesheet.submit();
        saveTimesheets();

        return timesheet;
    } catch (NumberFormatException e) {
        showAlert("Error", "Invalid input. Please enter valid numeric values for hours.");
        return null;
    }
}


    private void approveSelectedTimesheet() {
        Timesheet selectedTimesheet = timesheetListView.getSelectionModel().getSelectedItem();
        if (selectedTimesheet != null && !selectedTimesheet.isApproved()) {
            selectedTimesheet.approve();
            saveTimesheets();
            updateTimesheetListView();
        } else {
            showAlert("Error", "Select a submitted timesheet to approve.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private List<Employee> loadEmployees() {
        try {
            return Files.lines(Paths.get(EMPLOYEES_FILE))
                    .map(line -> {
                        String[] parts = line.split(",");
                        return new Employee(parts[0], parts[1]);
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private List<Timesheet> loadTimesheets() {
        try {
            return Files.lines(Paths.get(TIMESHEETS_FILE))
                    .map(line -> {
                        String[] parts = line.split(",");
                        Employee employee = findEmployeeByUsername(parts[0]);
                        int[] hoursWorked = new int[parts.length - 2];
                        for (int i = 2; i < parts.length; i++) {
                            hoursWorked[i - 2] = Integer.parseInt(parts[i]);
                        }
                        return new Timesheet(employee, LocalDate.parse(parts[1]), hoursWorked);
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private Employee findEmployeeByUsername(String username) {
        return employees.stream()
                .filter(employee -> employee.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private void saveEmployees() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EMPLOYEES_FILE))) {
            employees.forEach(employee -> writer.println(employee.getUsername() + "," + employee.getDepartment()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTimesheets() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TIMESHEETS_FILE))) {
            timesheets.forEach(timesheet -> {
                writer.print(timesheet.getEmployee().getUsername() + ",");
                writer.print(timesheet.getWeekStartDate() + ",");
                for (int hours : timesheet.getHoursWorked()) {
                    writer.print(hours + ",");
                }
                writer.print(timesheet.isSubmitted() + ",");
                writer.println(timesheet.isApproved());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
