/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulategateways;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author 70136
 */
public class SimulateGateways {
    static String server="219.91.49.21";
    static int entry=5000;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Random random=new Random();
        Timer timer1=new Timer(5000,(e) -> {
            System.out.println("gataway01");
            try {
                Socket socket=new Socket(server, entry);
                OutputStream outputStream=socket.getOutputStream();
                PrintWriter printWriter=new PrintWriter(outputStream, true);
                String json="[{\"id\": \"sensor01\", \"type\": \"tm\", \"value\": "+(random.nextFloat()*100)+", \"status\": \"test\"}, {\"id\": \"sensor02\", \"type\": \"tm\", \"value\":"+(random.nextFloat()*100)+", \"status\": \"test\"}]";
                String urlParameters ="target=gateway01&gatewayid=00&status=test&time="+LocalDateTime.now().toString()+"&obj="+json;
                printWriter.println(urlParameters);
                printWriter.flush();
                printWriter.close();
                outputStream.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulateGateways.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        timer1.start();
        //
//        Timer timer2=new Timer(5000,(e) -> {
//            System.out.println("gataway02");
//            try {
//                Socket socket=new Socket(server, entry);
//                OutputStream outputStream=socket.getOutputStream();
//                PrintWriter printWriter=new PrintWriter(outputStream, true);
//                String json="[{\"id\": \"sensor01\", \"type\": \"tm\", \"value\": "+(random.nextFloat()*100)+", \"status\": \"test\"}, {\"id\": \"sensor02\", \"type\": \"tm\", \"value\":"+(random.nextFloat()*100)+", \"status\": \"test\"}]";
//                String urlParameters ="target=gateway01&gatewayid=00&status=test&time="+LocalDateTime.now().toString()+"&obj="+json;
//                printWriter.println(urlParameters);
//                printWriter.flush();
//                printWriter.close();
//                outputStream.close();
//                socket.close();
//            } catch (IOException ex) {
//                Logger.getLogger(SimulateGateways.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
//        timer2.start();
        JFrame frame=new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
