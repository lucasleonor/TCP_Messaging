package br.com.training.threads.messaging.server.service;

import br.com.training.threads.messaging.server.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private ClientDao dao;

    @Autowired
    public ClientService(ClientDao dao) {
        this.dao = dao;
    }

}
