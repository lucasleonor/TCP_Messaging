package br.com.training.threads.messaging.client;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class InputListener implements Runnable {

    private final PrintStream outputWriter;
    private final Scanner keyboardInput;

    public InputListener(OutputStream outputStream) {
        outputWriter = new PrintStream(outputStream);
        keyboardInput = new Scanner(System.in);
    }

    @Override
    public void run() {
        while(keyboardInput.hasNextLine()){
            String input = keyboardInput.nextLine().trim();
            if(!input.isBlank()) outputWriter.println(input);
        }

        keyboardInput.close();
        outputWriter.close();
    }
}
