package br.com.training.threads.messaging.server.service;

import br.com.training.threads.messaging.server.dao.ClientDao;
import br.com.training.threads.messaging.server.dao.MessageDao;
import br.com.training.threads.messaging.server.model.Client;
import br.com.training.threads.messaging.server.model.Message;
import br.com.training.threads.messaging.server.util.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MessagingManager {
    private final ExecutorService threadPool;
    private final Map<String, ClientRunnable> clientMap;
    private final ClientDao clientDao;
    private final MessageDao messageDao;

    @Autowired
    public MessagingManager(ClientDao clientDao, MessageDao messageDao) {
        this.clientDao = clientDao;
        this.messageDao = messageDao;
        threadPool = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler(new ExceptionHandler());
            return thread;
        });
        clientMap = new ConcurrentHashMap<>();
    }

    public void newConnection(Socket socket) throws IOException {
        threadPool.execute(new ClientRunnable(socket, this));
    }

    public void stop() {
        for (ClientRunnable runnable : clientMap.values()) {
            runnable.disconnect();
        }
        threadPool.shutdown();
    }

    public void disconnect(ClientRunnable runnable) {
        clientMap.remove(runnable.getClient().getUsername());
        runnable.disconnect();
    }

    public boolean checkUserConnected(String name) {
        return clientMap.containsKey(name);
    }

    public void connect(ClientRunnable runnable) {
        Client client = runnable.getClient();
        clientMap.put(client.getUsername(), runnable);
    }

    public boolean sendMessage(Client client, String recipientUsername, String messageText) {
        ClientRunnable recipientRunnable = clientMap.get(recipientUsername);
        if (recipientRunnable != null) {
            Message message = new Message(messageText, recipientRunnable.getClient(), client);
            messageDao.save(message);
            recipientRunnable.sendMessage(client.getUsername(), messageText);
            return true;
        }
        return false;
    }

    public Optional<Client> getClient(String username) {
        return clientDao.findOne(Example.of(new Client(username)));
    }

    public Client register(Client client) {
        return clientDao.save(client);
    }
}
