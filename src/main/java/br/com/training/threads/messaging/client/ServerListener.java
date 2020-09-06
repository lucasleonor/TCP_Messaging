package br.com.training.threads.messaging.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Scanner;

public class ServerListener implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListener.class);
    private final Scanner inputReader;

    public ServerListener(InputStream inputStream) {
        LOGGER.info("Initializing server listener");
        inputReader = new Scanner(inputStream);
    }

    @Override
    public void run() {
        while (inputReader.hasNextLine()) {
            String input = inputReader.nextLine();
            if ("disconnect".equalsIgnoreCase(input)) break;
            LOGGER.info(input);
        }
        LOGGER.info("Closing connection with server...");
        inputReader.close();
    }
}
