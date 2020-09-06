package br.com.training.threads.messaging.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class MessagingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingClient.class);

    private final Socket clientSocket;

    public MessagingClient() throws IOException {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket("localhost", 12345);
            LOGGER.info("Connected to server on port: {}", clientSocket.getPort());
        } catch (ConnectException e) {
            LOGGER.error("Could not connect to the server, is it running?");
            System.exit(1);
        }
        this.clientSocket = clientSocket;
    }

    public void run() throws InterruptedException, IOException {
        Thread serverListener = new Thread(new ServerListener(clientSocket.getInputStream()));
        serverListener.start();

        InputListener inputListener = new InputListener(clientSocket.getOutputStream());
        new Thread(inputListener).start();

        serverListener.join();

        inputListener.stop();
        LOGGER.info("Closing connection...");
        clientSocket.close();
        LOGGER.info("Connection closed, shutting down");
    }
}
