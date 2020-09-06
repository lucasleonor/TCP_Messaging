package br.com.training.threads.messaging.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerListener implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListener.class);
    private final InputStream inputStream;

    public ServerListener(InputStream inputStream) {
        LOGGER.info("Initializing server listener");
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (true) {
                if (inputReader.ready()) {
                    String input = inputReader.readLine().trim();
                    if ("disconnect".equalsIgnoreCase(input)) break;
                    LOGGER.info(input);
                }
            }
            LOGGER.info("Closing connection with server...");
        } catch (IOException e) {
            LOGGER.error("IO Error: {}", e.getMessage());
        }
    }
}
