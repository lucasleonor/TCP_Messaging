package br.com.training.threads.messaging.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class MessagingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingClient.class);

    private final Socket clientSocket;

    public MessagingClient() throws IOException {
        clientSocket = new Socket("localhost", 12345);
        LOGGER.info("Connected to server on port: {}", clientSocket.getPort());
    }

    public void run() throws InterruptedException, IOException {
        Thread serverListener = new Thread(new ServerListener(clientSocket.getInputStream()));
        Thread inputListener = new Thread(new InputListener(clientSocket.getOutputStream()));

        serverListener.start();
        inputListener.start();

        serverListener.join();
        LOGGER.info("Closing connection...");
        clientSocket.close();
        LOGGER.info("Connection closed, shutting down");
    }
}
