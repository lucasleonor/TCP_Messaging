package br.com.training.threads.messaging.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessagingClientRunner implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MessagingClientRunner.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		MessagingClient messagingServer = new MessagingClient();
		messagingServer.run();
	}
}
