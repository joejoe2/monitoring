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
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author 70136
 */
public class Receiver {

    //logging unit
    JTextArea console;

    //next pipeline component
    Processor processor;

    //running flag
    boolean isruning = false;

    //server socket obj
    ServerSocket server;

    //for auto timeout checking 
    String[] defaultStr;
    HashMap<String, Integer> gateways;
    String[] pre;
    String[] next;
    Timer checker;
    int timeout = 10000;//10s

    //accept regx pattern
    String pattern = "target=devices[0-9]+&devicesid=[0-9]+&status=.+&time=.+&obj=.+";

    /**
     * init the receiver component
     *
     * @param console logging unit
     * @param defaultStr default value array
     *
     */
    public Receiver(MyConsole console, String[] defaultStr) {
        //pass args
        this.console = console;
        this.defaultStr = defaultStr;

        //init auto timeout checking
        LocalDateTime now = LocalDateTime.now();
        pre = new String[defaultStr.length];
        next = new String[defaultStr.length];
        gateways = new HashMap<>();
        for (int i = 0; i < next.length; i++) {
            gateways.put(defaultStr[i].substring(defaultStr[i].indexOf("target=") + 7, defaultStr[i].indexOf("&")), i);
            pre[i] = "";
            next[i] = now.toString();
        }
    }

    /**
     * stop the receiver componet(also the following pipline components)
     */
    public void stop() {
        //clear running flag
        isruning = false;

        //stop auto timeout checker
        checker.stop();

        //close socket server
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * bind the next pipeline component and start working
     *
     * @param processor Processor uint
     */
    public void bind(Processor processor) {
        //bind processor
        this.processor = processor;

        //set running flag
        isruning = true;

        //start main work 
        main();

        //start auto timeout checking
        checkTimeOut();

        //log start info
        console.append("Receiver start at " + LocalDateTime.now() + "\n");
    }

    /**
     * start to auto timeout check
     */
    synchronized void checkTimeOut() {
        checker = new Timer(timeout, (e) -> {//check per timeout interval
            Pool.execute(() -> {
                //for each device target
                for (int i = 0; i < next.length; i++) {
                    if (!pre[i].equals(next[i])) {//if pre value != new value
                        //update pre value
                        pre[i] = next[i];
                    } else {
                        //pass default value of device target itself to processor
                        LocalDateTime now = LocalDateTime.now();
                        processor.add(defaultStr[i].replaceFirst("&time&", "&time=" + now + "&").replace("\"value\":0", "\"value\":\"unknown\""));

                        //log no reponse info
                        console.append(defaultStr[i].split("&")[0] + " is no response at " + now + "\n");
                    }
                }

            });
        });
        checker.start();
    }

    /**
     * start to receive target device data
     */
    void main() {
        try {
            // 監聽和接收客戶端連線
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            server = (SSLServerSocket) factory.createServerSocket(5000);

            //satrt server socket
            //server = new ServerSocket(5000);
            Pool.execute(() -> {//loop thread
                while (isruning) {//when is running
                    try {
                        //get incomming client
                        Socket client = server.accept();
                        Pool.execute(() -> {//process thread
                            try {
                                //open input streams
                                InputStream inputStream = client.getInputStream();
                                InputStreamReader inrReader = new InputStreamReader(inputStream);
                                BufferedReader reader = new BufferedReader(inrReader);

                                //read in data
                                String data = reader.readLine();
                                LocalDateTime now = LocalDateTime.now();

                                if (data.matches(pattern)) {//if is correct format
                                    //update new value holder
                                    next[gateways.get(data.substring(data.indexOf("target=") + 7, data.indexOf("&")))] = now.toString();
                                    //pass to processor
                                    processor.add(data);
                                    //log receive info
                                    console.append("receive data => " + data.split("&")[0] + " at " + now + "\n");
                                } else {//if is uncorrect format
                                    //log discard info
                                    console.append("discard uncorrect data " + data + " at " + now + "\n");
                                }

                                //close streams
                                inputStream.close();
                                inrReader.close();
                                reader.close();
                                client.close();
                            } catch (Exception e) {
                                console.append(e.toString() + "\n");
                            }
                        });

                    } catch (Exception ex) {
                        if (!server.isClosed()) {
                            console.append(ex.toString() + "\n");
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            console.append(ex.toString() + "\n");
        }
    }
}
