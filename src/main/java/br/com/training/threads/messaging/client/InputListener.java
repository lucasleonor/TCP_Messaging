package br.com.training.threads.messaging.client;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputListener implements Runnable {

    private final AtomicBoolean running;
    private final OutputStream outputStream;

    public InputListener(OutputStream outputStream) {
        this.outputStream = outputStream;
        running = new AtomicBoolean(false);
    }

    @SneakyThrows
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
        }
    }

    public void stop() {
        running.set(false);
    }

}
