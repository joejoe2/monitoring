/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httppost;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.css.StyleOrigin.USER_AGENT;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author 70136
 */
public class HttpPOST {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Timer timer=new Timer(5000, (e) -> {
            try {
            // TODO code application logic here
            sendPost();
        } catch (Exception ex) {
            Logger.getLogger(HttpPOST.class.getName()).log(Level.SEVERE, null, ex);
        }
        });
        timer.start();
        new JFrame().setVisible(true);
    }
    
     private static void sendPost() throws Exception {

        String url = "http://localhost/commit.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");

        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
        String json="[{\"id\": \"sensor01\", \"type\": \"tm\", \"value\": "+((new Random()).nextFloat()*100)+", \"status\": \"test\"}, {\"id\": \"sensor02\", \"type\": \"tm\", \"value\": 14, \"status\": \"test\"}]";
        String urlParameters ="target=gateway01&gatewayid=00&status=test&time="+LocalDateTime.now().toString()+"&obj="+json;
        //发送Post请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
        System.out.println(response.toString());

    }
}
