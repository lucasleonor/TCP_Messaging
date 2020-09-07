package br.com.training.threads.messaging.server;

import br.com.training.threads.messaging.server.service.MessagingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MessagingServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingServer.class);

    private final ServerSocket serverSocket;
    private final AtomicBoolean running;
    private final MessagingManager messagingManager;
    private final ConfigurableApplicationContext context;

    @Autowired
    public MessagingServer(MessagingManager messagingManager, ConfigurableApplicationContext context) throws IOException {
        this.context = context;
        serverSocket = new ServerSocket(12345);

        this.messagingManager = messagingManager;
        running = new AtomicBoolean(false);
    }

    public void run() throws IOException {
        LOGGER.info("Starting Server");
        running.set(true);
        while(running.get()){
            try {
                LOGGER.info("Waiting for connection...");
                Socket socket = serverSocket.accept();
                LOGGER.info("New client connected at port: {}", socket.getPort());

                messagingManager.newConnection(socket);
            } catch (SocketException e) {
                LOGGER.error("SocketException, is server running? {}", running);
            }
        }
    }

    public void stop() throws IOException {
        LOGGER.info("Stopping Server");
        running.set(false);
        messagingManager.stop();
        serverSocket.close();
        LOGGER.info("Server won't accept any more connections, waiting for clients to close");
        context.close();
    }
}
