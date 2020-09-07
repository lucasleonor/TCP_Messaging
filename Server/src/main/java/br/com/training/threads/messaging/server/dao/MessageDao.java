package br.com.training.threads.messaging.server.dao;


import br.com.training.threads.messaging.server.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDao extends JpaRepository<Message, Integer> {

}
