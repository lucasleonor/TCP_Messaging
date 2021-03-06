package br.com.training.threads.messaging.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class MessagingServerRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingServerRunner.class);
    private final MessagingServer messagingServer;

    public static void main(String[] args) {
        SpringApplication.run(MessagingServerRunner.class, args);
    }

    @Autowired
    public MessagingServerRunner(MessagingServer messagingServer) {
        this.messagingServer = messagingServer;
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(() -> {
            boolean running = true;
            LOGGER.info("To shut the server down type 'shutdown', all clients will be disconnected");
            try (BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in))) {
                while (running) {
                    if (keyboardInput.ready()) {
                        String line = keyboardInput.readLine().trim();
                        if ("shutdown".equalsIgnoreCase(line)) {
                            try {
                                messagingServer.stop();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            running = false;
                        } else if (!line.isEmpty()) {
                            LOGGER.info("Invalid command {}", line);
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("IO Error: {}", e.getMessage());
            }
        }).start();

        messagingServer.run();
    }
}
