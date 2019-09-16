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
    String webserver = "";
    String db = "";
    String bot = "";
    String firebase = "";

    public CommitUnit(JTextArea console, RecordLog recordLog) {
        this.console = console;
        this.recordLog = recordLog;

        linkedList = new LinkedList<>();
    }

    void bind(String webserver, String db, String bot, String firebase) {
        this.webserver = webserver;
        this.db = db;
        this.bot = bot;
        this.firebase = firebase;
        console.append("CommitUnit start at " + LocalDateTime.now() + "\n");
    }

    synchronized void add(String data) {
        new Thread(() -> {
            linkedList.add(data);
            if (!linkedList.isEmpty()) {
                String dataIN = linkedList.pollFirst();
                try {
                    send_to_webservser(data);
                } catch (Exception e) {
                    console.append(e.toString() + "\n");
                }

            }
        }).start();
    }

    void update_websever_cfg(String[] data) {
        if (webserver.equals("")) {
            return;
        }
        //clear
        try {
            URL obj = new URL(webserver + "/clear_devices_cfg.php");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            //添加请求头
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            console.append("init websever cfg at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
        } catch (Exception ex) {
            console.append(ex + "\n");
            return;
        }
        int index=0;
        //update
        for (String cfg : data) {
            try {
                URL obj = new URL(webserver + "/set_devices_cfg.php");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setConnectTimeout(5000);
                //添加请求头
                con.setRequestMethod("POST");

                //发送Post请求
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(cfg);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                console.append("update websever cfg("+(++index)+"/"+data.length+") at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
                
            } catch (Exception ex) {
                console.append(ex + "\n");
                break;
            }
        }

    }

    void update_db_cfg(String[] data) {
        if (db.equals("")) {
            return;
        }
        //clear
        try {
            URL obj = new URL(db + "clear_devices_cfg.php");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            //添加请求头
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            console.append("init db cfg at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
        } catch (Exception ex) {
            console.append(ex + "\n");
            return;
        }
        int index=0;
        for (String cfg : data) {

            //update
            try {
                URL obj = new URL(db + "set_devices_cfg.php");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setConnectTimeout(5000);
                //添加请求头
                con.setRequestMethod("POST");

                //发送Post请求
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(cfg);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                console.append("update db cfg("+(++index)+"/"+data.length+") at "+ LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
                
            } catch (Exception ex) {
                console.append(ex + "\n");
                break;
            }
        }
    }

    void send_to_db(String data) {
        if (db.equals("")) {
            return;
        }
    }

    void send_to_bot(String data) {
        if (bot.equals("")) {
            return;
        }
    }

    void send_to_webservser(String data) throws Exception {
        if (webserver.equals("")) {
            return;
        }
        URL obj = new URL(webserver+"/commit.php");
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
        console.append("commit data => " + data + " at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
        recordLog.append(data + "\n");

    }
}
