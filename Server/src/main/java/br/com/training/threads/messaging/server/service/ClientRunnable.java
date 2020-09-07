package br.com.training.threads.messaging.server.service;

import br.com.training.threads.messaging.server.model.Client;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRunnable.class);

    private final MessagingManager messagingManager;
    private final AtomicBoolean running;
    private final PrintStream outputWriter;
    private final BufferedReader inputReader;

    @Getter
    private Client client;

    public ClientRunnable(Socket socket, MessagingManager messagingManager) throws IOException {
        this.messagingManager = messagingManager;
        outputWriter = new PrintStream(socket.getOutputStream());
        inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        running = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        running.set(true);
        try {
            prepareClient();
            LOGGER.info("New client: {}", client.getUsername());

            while (running.get()) {
                if (inputReader.ready()) {
                    String input = fetchMessage();
                    if ("disconnect".equalsIgnoreCase(input)) {
                        break;
                    }
                    parseMessage(input);
                }
            }
            LOGGER.info("Client '{}' disconnected", client.getUsername());
        } catch (IOException e) {
            LOGGER.info("IO Error: {}", e.getMessage());
        } finally {
            messagingManager.disconnect(this);
            outputWriter.close();
            try {
                inputReader.close();
            } catch (IOException e) {
                LOGGER.error("Error while closing inputReader: {}", e.getMessage());
            }
        }
    }

    private void prepareClient() throws IOException {
        String username = getUsername();
        Optional<Client> client = messagingManager.getClient(username);
        if (client.isPresent()) {
            sendMessage("Welcome back " + username + "!\n" +
                    "To send messages follow the pattern '{recipient}:{message}'");
            this.client = client.get();
        } else {
            sendMessage("Welcome " + username + "!\n" +
                    "To send messages follow the pattern '{recipient}:{message}'");
            this.client = messagingManager.register(new Client(username));
        }
        messagingManager.connect(this);
    }

    private String getUsername() throws IOException {
        sendMessage("Please, enter your username");
        String username = "";
        while (username.isBlank() && running.get()) {
            if (inputReader.ready()) {
                username = fetchMessage();
                if (messagingManager.checkUserConnected(username)) {
                    sendMessage("The user '" + username + "' is already connected.");
                    username = "";
                }
            }
        }
        return username;
    }

    private String fetchMessage() throws IOException {
        try {
            return inputReader.readLine().trim();
        } catch (SocketException e) {
            disconnect();
            return "";
        }
    }

    private void parseMessage(String text) {
        String[] input = text.split(":", 2);
        if (input.length != 2) {
            sendMessage("Invalid input, it should follow the pattern '{recipient}:{message}'");
            return;
        }
        String recipient = input[0];
        String message = input[1].trim();
        if (messagingManager.sendMessage(client, recipient, message)) {
            sendMessage("Message sent successfully");
            LOGGER.info("New message from '{}', to '{}': {}", client.getUsername(), recipient, message);
        } else {
            sendMessage("Could not find recipient: " + recipient);
        }
    }

    public void sendMessage(String from, String message) {
        outputWriter.println(from + ": " + message);
    }

    private void sendMessage(String s) {
        outputWriter.println(s);
    }

    public void disconnect() {
        running.set(false);
        sendMessage("disconnect");
    }
}
