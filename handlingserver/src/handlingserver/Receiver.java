/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import javax.swing.JTextArea;

/**
 *
 * @author 70136
 */
public class Receiver {

    JTextArea console;
    Processor processor;
    boolean isruning = false;

    public Receiver(JTextArea console) {
        this.console = console;
        console.append("Receiver start at " + LocalDateTime.now() + "\n");
    }

    void stop() {
        isruning = false;
    }

    void bind(Processor processor) {
        this.processor = processor;
        isruning = true;
        main();
    }

    void main() {
        try {
            ServerSocket server = new ServerSocket(5000);
            new Thread(() -> {
                while (isruning) {
                    try {
                        Socket client = server.accept();
                        InputStream inputStream = client.getInputStream();
                        InputStreamReader inrReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(inrReader);
                        //
                        String data = reader.readLine();
                        processor.add(data);
                        console.append("receive data => " + data + " at " + LocalDateTime.now() + "\n");
                        //
                        inputStream.close();
                        inrReader.close();
                        reader.close();
                        client.close();
                    } catch (IOException ex) {
                        console.append(ex.toString() + "\n");
                    }
                }
            }).start();
        } catch (IOException ex) {
            console.append(ex.toString() + "\n");
        }
    }
}
