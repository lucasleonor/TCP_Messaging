package br.com.training.threads.messaging.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "MESSAGE")
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "TEXT")
    private String text;
    @ManyToOne
    @JoinColumn(name="RECIPIENT", nullable=false)
    private Client recipient;
    @ManyToOne
    @JoinColumn(name="SENDER", nullable=false)
    private Client sender;

    public Message(String text, Client recipient, Client sender) {
        this.text = text;
        this.recipient = recipient;
        this.sender = sender;
    }
}
