package br.com.training.threads.messaging.server.service;

import br.com.training.threads.messaging.server.Client;
import br.com.training.threads.messaging.server.util.ExceptionHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MessagingManager {
    private final ExecutorService threadPool;
    private final Map<String, Client> clientMap;

    public MessagingManager() {
        threadPool = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler(new ExceptionHandler());
            return thread;
        });
        clientMap = new ConcurrentHashMap<>();
    }

    public void newConnection(Socket socket) throws IOException {
        threadPool.execute(new ClientRunnable(socket.getOutputStream(), socket.getInputStream(), this));
    }

    public void stop() {
        for (Client client : clientMap.values()) {
            client.disconnect();
        }
        threadPool.shutdown();
    }

    public void disconnect(Client client) {
        clientMap.remove(client.getName());
        client.disconnect();
    }

    public boolean checkUsername(String name) {
        return clientMap.containsKey(name);
    }

    public void connect(Client client) {
        clientMap.put(client.getName(), client);
    }

    public Client get(String name) {
        return clientMap.get(name);
    }
}
