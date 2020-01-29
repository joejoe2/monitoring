/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acceptentry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author 70136
 */
public class Handler {

    String server;
    int port;
    String key = "aa43235167123456";
    SSLSocketFactory socketFactory;
    String valid = "validate code is dmd5464fas4e629DF";

    public Handler(String server, int port) {

        this.server = server;
        this.port = port;
        try {
            socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handle(Socket client) {
        new Thread(() -> {
            try {
                InputStream in = client.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(isr);
                String input = AES128.decrypt(key, reader.readLine());
                if (input.contains(valid)) {
                    input=input.substring(valid.length());
                    System.out.println(input);
                    send(input);
                }
                reader.close();
                isr.close();
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    void send(String data) {
        new Thread(() -> {
            try {
                Socket socket = (SSLSocket) socketFactory.createSocket(server, port);
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                printWriter.println(data);
                printWriter.flush();
                printWriter.close();
                outputStream.close();
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
