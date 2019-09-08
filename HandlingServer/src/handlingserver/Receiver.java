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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author 70136
 */
public class Receiver {

    JTextArea console;
    Processor processor;
    boolean isruning = false;
    ServerSocket server;

    String[] defaultStr;
    HashMap<String, Integer> gateways;
    String[] pre;
    String[] next;
    Timer checker;
    int timeout=10000;//10s

    public Receiver(JTextArea console, String[] defaultStr) {
        this.console = console;
        this.defaultStr = defaultStr;
        LocalDateTime now = LocalDateTime.now();
        console.append("Receiver start at " + now + "\n");
        pre = new String[defaultStr.length];
        next = new String[defaultStr.length];
        gateways=new HashMap<>();
        for (int i = 0; i < next.length; i++) {
            gateways.put(defaultStr[i].substring(defaultStr[i].indexOf("target=")+7,defaultStr[i].indexOf("&")),i);
            pre[i]="";
            next[i] = now.toString();
        }
    }

    void stop() {
        isruning = false;
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        checker.stop();
    }

    void bind(Processor processor) {
        this.processor = processor;
        isruning = true;
        main();
        checkTimeOut();
    }

    void checkTimeOut() {
        checker = new Timer(timeout, (e) -> {
            new Thread(() -> {
                synchronized (next) {
                    for (int i = 0; i < next.length; i++) {
                        if (!pre[i].equals(next[i])) {
                            pre[i] = next[i];
                        } else {
                            LocalDateTime now=LocalDateTime.now();
                            processor.add(defaultStr[i].replaceFirst("&time&","&time="+now+"&").replace("\"value\":0","\"value\":\"unknown\""));
                            console.append(defaultStr[i].split("&")[0]+ " is no response at " + now + "\n");
                        }
                    }
                }
            }).start();
        });
        checker.start();
    }

    void main() {
        try {
            server = new ServerSocket(5000);
            new Thread(() -> {
                while (isruning) {
                    try {
                        Socket client = server.accept();
                        InputStream inputStream = client.getInputStream();
                        InputStreamReader inrReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(inrReader);

                        String data = reader.readLine();
                        LocalDateTime now=LocalDateTime.now();
                        next[gateways.get(data.substring(data.indexOf("target=")+7,data.indexOf("&")))]=now.toString();
                        processor.add(data);
                        
                        console.append("receive data => " + data + " at " + now + "\n");

                        inputStream.close();
                        inrReader.close();
                        reader.close();
                        client.close();

                    } catch (Exception ex) {
                        //ex.printStackTrace();
                        if (!server.isClosed()) {
                            console.append(ex.toString() + "\n");
                        }
                    }
                }
            }).start();
        } catch (IOException ex) {
            ex.printStackTrace();
            console.append(ex.toString() + "\n");
        }
    }
}
