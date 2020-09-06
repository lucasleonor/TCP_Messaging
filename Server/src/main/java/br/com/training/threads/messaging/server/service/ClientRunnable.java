package br.com.training.threads.messaging.server.service;

import br.com.training.threads.messaging.server.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRunnable.class);
    private final OutputStream outputStream;
    private final InputStream inputStream;
    private final MessagingManager service;
    private final AtomicBoolean running;
    private Client client;

    public ClientRunnable(OutputStream outputStream, InputStream inputStream, MessagingManager service) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.service = service;
        running = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        running.set(true);
        try (PrintStream outputWriter = new PrintStream(outputStream);
             BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String name = prepareClient(inputReader, outputWriter);
            LOGGER.info("New client: {}", name);

            while (running.get()) {
                if (inputReader.ready()) {
                    String input = inputReader.readLine().trim();
                    if ("disconnect".equalsIgnoreCase(input)) {
                        break;
                    }
                    readMessage(name, input, outputWriter);
                }
            }
            LOGGER.info("Client '{}' disconnected", name);
            service.disconnect(client);
        } catch (IOException e) {
            LOGGER.info("IO Error: {}", e.getMessage());
        }
    }

    private String prepareClient(BufferedReader input, PrintStream outputWriter) throws IOException {
        outputWriter.println("Please, enter your name");
        String name = "";
        while (name.isBlank() && running.get()) {
            if (input.ready()) {
                name = input.readLine().trim();
                if (service.checkUsername(name)) {
                    outputWriter.println("The name '" + name + "' is already being used. Try another one");
                    name = "";
                }
            }
        }
        outputWriter.println("Welcome " + name + "!\nTo send messages follow the pattern '{recipient}:{message}'");
        client = new Client(name, outputWriter, this);

        service.connect(client);
        return name;
    }

    private void readMessage(String name, String text, PrintStream outputWriter) {
        String[] input = text.split(":", 2);
        if (input.length != 2) {
            outputWriter.println("Invalid input, it should follow the pattern '{recipient}:{message}'");
            return;
        }
        String recipient = input[0];
        if (!service.checkUsername(recipient)) {
            outputWriter.println("Could not find recipient: " + recipient);
            return;
        }
        String message = input[1].trim();
        service.get(recipient).sendMessage(name, message);
        outputWriter.println("Message sent successfully");
        LOGGER.info("New message from '{}', to '{}': {}", name, recipient, message);
    }

    public void stop() {
        running.set(false);
    }
}
