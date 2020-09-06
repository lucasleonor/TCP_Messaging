package br.com.training.threads.messaging.server;

import lombok.Getter;

import java.io.PrintStream;

public class Client {
    @Getter
    private final String name;
    private final PrintStream outputWriter;

    public Client(String name, PrintStream outputWriter) {
        this.name = name;
        this.outputWriter = outputWriter;
    }

    public void sendMessage(String from, String message) {
        outputWriter.println(from + ": " + message);
    }

    public void disconnect() {
        outputWriter.println("disconnect");
    }
}
