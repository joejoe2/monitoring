/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedList;
import javax.swing.JTextArea;

/**
 *
 * @author 70136
 */
public class CommitUnit {

    JTextArea console;
    RecordLog recordLog;
    LinkedList<String> linkedList;

    public CommitUnit(String addr, JTextArea console,RecordLog recordLog) {
        this.console = console;
        this.recordLog=recordLog;
        console.append("CommitUnit start at " + LocalDateTime.now() + "\n");
        linkedList = new LinkedList<>();
    }

    void bind() {

    }

    synchronized void add(String data) {
        new Thread(() -> {
            linkedList.add(data);
            if (!linkedList.isEmpty()) {
                String dataIN=linkedList.pollFirst();
                try {
                    sendPost(data);
                } catch (Exception e) {
                    console.append(e.toString()+"\n");
                }
                
            }
        }).start();
    }
    
    private void sendPost(String data) throws Exception {

        String url = "http://showdata.nctu.me/commit.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setConnectTimeout(5000);
        //添加请求头
        con.setRequestMethod("POST");

        //发送Post请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        console.append("commit data => "+data+" at "+LocalDateTime.now()+" wtih response code="+responseCode+"\n");
        recordLog.append(data+"\n");
//        System.out.println("\nSending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + urlParameters);
//        System.out.println("Response Code : " + responseCode);
//        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        //打印结果
//        System.out.println(response.toString());

    }
}
