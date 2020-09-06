package br.com.training.threads.messaging.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Scanner;

@SpringBootApplication
public class MessagingServerRunner implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MessagingServerRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        MessagingServer messagingServer = new MessagingServer();

        new Thread(() -> {
            Scanner keyboardInput = new Scanner(System.in);
            while (keyboardInput.hasNextLine()) {
                String line = keyboardInput.nextLine().trim();
                if ("shutdown".equalsIgnoreCase(line)) {
                    try {
                        messagingServer.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }).start();

        messagingServer.run();
    }
}
