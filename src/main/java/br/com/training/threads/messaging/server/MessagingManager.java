package br.com.training.threads.messaging.server;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessagingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingManager.class);
    private final ExecutorService threadPool;
    private final Map<String, Client> clientMap;
    private final AtomicBoolean running;

    public MessagingManager() {
        threadPool = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler(new ExceptionHandler());
            return thread;
        });
        clientMap = new ConcurrentHashMap<>();
        running = new AtomicBoolean(false);
    }

    public void newConnection(Socket socket) {
        threadPool.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                running.set(true);
                try (PrintStream outputWriter = new PrintStream(socket.getOutputStream());
                     BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
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
                    Client client = clientMap.remove(name);
                    client.disconnect();
                }
            }

            private String prepareClient(BufferedReader input, PrintStream outputWriter) throws IOException {
                outputWriter.println("Please, enter your name");
                String name = "";
                while (name.isBlank() && running.get()) {
                    if (input.ready()) {
                        name = input.readLine().trim();
                        if (clientMap.containsKey(name)) {
                            outputWriter.println("The name '" + name + "' is already being used. Try another one");
                            name = "";
                        }
                    }
                }
                outputWriter.println("Welcome " + name + "!\nTo send messages follow the pattern '{recipient}:{message}'");
                Client client = new Client(name, outputWriter);

                clientMap.put(name, client);
                return name;
            }

            private void readMessage(String name, String text, PrintStream outputWriter) {
                String[] input = text.split(":", 2);
                if (input.length != 2) {
                    outputWriter.println("Invalid input, it should follow the pattern '{recipient}:{message}'");
                    return;
                }
                String recipient = input[0];
                if (!clientMap.containsKey(recipient)) {
                    outputWriter.println("Could not find recipient: " + recipient);
                    return;
                }
                String message = input[1].trim();
                clientMap.get(recipient).sendMessage(name, message);
                outputWriter.println("Message sent successfully");
                LOGGER.info("New message from '{}', to '{}': {}", name, recipient, message);
            }
        });
    }

    public void stop() {
        running.set(false);
        for (Client client : clientMap.values()) {
            client.disconnect();
        }
        threadPool.shutdown();
    }
}
