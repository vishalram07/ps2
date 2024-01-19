package com.dc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class Client extends Application {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;

    private String clientName;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private TextField inputTextField;
    private TextArea chatArea;

    @Override
    public void start(Stage primaryStage) {
        

        
        inputTextField = new TextField();
        chatArea = new TextArea();
        chatArea.setEditable(false);

        VBox vBox = new VBox(10, chatArea, inputTextField);

        Scene scene = new Scene(vBox, 400, 300);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> System.exit(0));

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Client Name");
        dialog.setHeaderText("Enter your name:");
        dialog.setContentText("Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            clientName = name;
            connectToServer(clientName);
        });

        inputTextField.setOnAction(e -> {
            String message = inputTextField.getText();
            sendMessage(message);
            inputTextField.clear();
        });
        primaryStage.setTitle(clientName + " - Morse Code Chat - Client");
        primaryStage.show();
    }

    private void connectToServer(String clientName) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            output.writeUTF(clientName);
            output.flush();

            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            while (true) {
                String receivedMessage = input.readUTF();
                chatArea.appendText(receivedMessage + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
