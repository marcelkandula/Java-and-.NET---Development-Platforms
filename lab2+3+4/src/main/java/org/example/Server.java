package org.example;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;

public class Server {
    private static final int PORT = 12345;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Serwer uruchomiony na porcie " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Połączono z klientem: " + clientSocket.getInetAddress());
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (Exception e) {
            logger.severe("Błąd serwera: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            ResultsCollector received = (ResultsCollector) ois.readObject();
            displayResults(received);
        } catch (Exception e) {
            logger.severe("Błąd obsługi klienta: " + e.getMessage());
        }
    }


    private static void displayResults(ResultsCollector collector) {
        StringBuilder message = new StringBuilder();
        for (Result result : collector.getResults()) {
            message.append(result.toString()).append("\n");
        }
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "Wyniki", JOptionPane.INFORMATION_MESSAGE);
    }

}
