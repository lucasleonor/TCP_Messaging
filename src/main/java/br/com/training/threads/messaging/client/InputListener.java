package br.com.training.threads.messaging.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputListener implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputListener.class);
    private final AtomicBoolean running;
    private final OutputStream outputStream;

    public InputListener(OutputStream outputStream) {
        this.outputStream = outputStream;
        running = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        running.set(true);
        try (PrintStream outputWriter = new PrintStream(this.outputStream);
             BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in))) {
            while (running.get()) {
                if (keyboardInput.ready()) {
                    String input = keyboardInput.readLine().trim();
                    if (!input.isBlank()) outputWriter.println(input);
                }
            }
        } catch (IOException e) {
            LOGGER.error("IO Error: {}", e.getMessage());
        }
    }

    public void stop() {
        running.set(false);
    }

}
