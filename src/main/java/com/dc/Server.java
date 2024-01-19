package com.dc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 5000;
    private static Map<String, ObjectOutputStream> clients = new HashMap<>();

    private static final Map<Character, String> morseCodeMap = new HashMap<>();

    static {
        // Basic Morse code mapping
        morseCodeMap.put('A', ".-");
        morseCodeMap.put('B', "-...");
        morseCodeMap.put('C', "-.-.");
        morseCodeMap.put('D', "-..");
        morseCodeMap.put('E', ".");
        morseCodeMap.put('F', "..-.");
        morseCodeMap.put('G', "--.");
        morseCodeMap.put('H', "....");
        morseCodeMap.put('I', "..");
        morseCodeMap.put('J', ".---");
        morseCodeMap.put('K', "-.-");
        morseCodeMap.put('L', ".-..");
        morseCodeMap.put('M', "--");
        morseCodeMap.put('N', "-.");
        morseCodeMap.put('O', "---");
        morseCodeMap.put('P', ".--.");
        morseCodeMap.put('Q', "--.-");
        morseCodeMap.put('R', ".-.");
        morseCodeMap.put('S', "...");
        morseCodeMap.put('T', "-");
        morseCodeMap.put('U', "..-");
        morseCodeMap.put('V', "...-");
        morseCodeMap.put('W', ".--");
        morseCodeMap.put('X', "-..-");
        morseCodeMap.put('Y', "-.--");
        morseCodeMap.put('Z', "--..");
        morseCodeMap.put('1', ".----");
        morseCodeMap.put('2', "..---");
        morseCodeMap.put('3', "...--");
        morseCodeMap.put('4', "....-");
        morseCodeMap.put('5', ".....");
        morseCodeMap.put('6', "-....");
        morseCodeMap.put('7', "--...");
        morseCodeMap.put('8', "---..");
        morseCodeMap.put('9', "----.");
        morseCodeMap.put('0', "-----");
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            String clientName = input.readUTF();
            clients.put(clientName, output);
            System.out.println(clientName + " connected.");

            while (true) {
                String message = input.readUTF();
                String morseCode = encodeToMorseCode(message);
                sendMessageToOtherClients(clientName, message, morseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessageToOtherClients(String sender, String message, String morseCode) {
        clients.forEach((name, output) -> {
            if (!name.equals(sender)) {
                try {
                    String formattedMessage = String.format("%s (Morse: %s)", message, morseCode);
                    output.writeUTF(sender + ": " + formattedMessage);
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static String encodeToMorseCode(String message) {
        StringBuilder morseCodeBuilder = new StringBuilder();
        for (char c : message.toUpperCase().toCharArray()) {
            if (morseCodeMap.containsKey(c)) {
                morseCodeBuilder.append(morseCodeMap.get(c)).append(" "); // One blank between Morse-coded letters
            } else if (c == ' ') {
                morseCodeBuilder.append("   ");  // Three blanks between Morse-coded words
            }
        }
        return morseCodeBuilder.toString().trim();
    }
    
}
