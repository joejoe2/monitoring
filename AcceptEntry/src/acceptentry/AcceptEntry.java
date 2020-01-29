/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acceptentry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 70136
 */
public class AcceptEntry {

    static int acceptport = 6000;
    static String toserver = "showdata.nctu.me";
    static int toserverport = 5000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Handler handler = new Handler(toserver, toserverport);

        Thread mainthread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new  ServerSocket(acceptport);
                while (true) {
                    Socket client = serverSocket.accept();
                    handler.handle(client);
                }
            } catch (IOException ex) {
                Logger.getLogger(AcceptEntry.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        mainthread.start();
    }

}
