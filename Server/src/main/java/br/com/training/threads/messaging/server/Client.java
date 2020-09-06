package br.com.training.threads.messaging.server;

import br.com.training.threads.messaging.server.service.ClientRunnable;
import lombok.Getter;

import java.io.PrintStream;

public class Client {
    @Getter
    private final String name;
    private final PrintStream outputWriter;
    private final ClientRunnable clientRunnable;

    public Client(String name, PrintStream outputWriter, ClientRunnable clientRunnable) {
        this.name = name;
        this.outputWriter = outputWriter;
        this.clientRunnable = clientRunnable;
    }

    public void sendMessage(String from, String message) {
        outputWriter.println(from + ": " + message);
    }

    public void disconnect() {
        clientRunnable.stop();
        outputWriter.println("disconnect");
    }
}
