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
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JTextArea;

/**
 *
 * @author 70136
 */
public class CommitUnit {

    //logging units
    JTextArea console;
    RecordLog recordLog;
    //net address
    private String webserver = "";
    private String db = "";
    private String bot = "";
    private String botkey = "";
    private String firebase = "";

    /**
     * create CommitUnit with inited logging units
     * @param console MyConsole obj
     * @param recordLog RecordLog obj
     */
    public CommitUnit(MyConsole console, RecordLog recordLog) {
        //init logging units
        this.console = console;
        this.recordLog = recordLog;
        
        //ignore host name check, just verify certificate
        HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
    }

    /**
     * bind net address and start working
     *
     * @param webserver addr
     * @param db addr
     * @param bot addr
     * @param botkey key
     * @param firebase addr
     * @param defaultstr init cfg array
     */
    public void bind(String webserver, String db, String bot, String botkey, String firebase, String[] defaultstr) {
        //bind net address and start working
        this.webserver = webserver;
        this.db = db;
        this.bot = bot;
        this.botkey = botkey;
        this.firebase = firebase;
        console.append("CommitUnit start at " + LocalDateTime.now() + "\n");

        //update cfg
        Pool.execute(() -> {
            update_db_cfg(defaultstr);
        });
        Pool.execute(() -> {
            update_websever_cfg(defaultstr);
        });
    }

    /**
     * receive data and msg then send to diffrent address
     *
     * @param data send to webserver and db
     * @param msg send to firebase and bot
     */
    public void add(String data, String msg) {
        //create each thread to prevent from stucking
        Pool.execute(() -> {
            send_to_webservser(data);
        });
        Pool.execute(() -> {
            send_to_db(data);
        });
        Pool.execute(() -> {
            send_to_bot(msg);
        });
        Pool.execute(() -> {
            send_to_firebase(msg);
        });
    }

    /**
     * init webserver config
     *
     * @param String[] data is a default string array
     */
    void update_websever_cfg(String[] data) {
        //if adress is empty => return directly
        if (webserver.equals("")) {
            return;
        }

        //clear clear devices cfg
        try {
            URL obj = new URL(webserver + "/clear_devices_cfg.php");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            //add post header
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            console.append("init websever cfg at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
        } catch (Exception ex) {
            console.append(ex + "\n");
            return;
        }

        //update progress holder
        int index = 0;
        //update devices cfg
        for (String cfg : data) {
            try {
                URL obj = new URL(webserver + "/set_devices_cfg.php");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setConnectTimeout(5000);
                //add post header
                con.setRequestMethod("POST");

                //send post param
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(cfg);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                console.append("update websever cfg(" + (++index) + "/" + data.length + ") at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");

            } catch (Exception ex) {
                console.append(ex + "\n");
                break;
            }
        }

    }

    /**
     * init db config
     *
     * @param String[] data is a default string array
     */
    void update_db_cfg(String[] data) {
        //if adress is empty => return directly
        if (db.equals("")) {
            return;
        }

        //clear devices cfg
        try {
            URL obj = new URL(db + "/clear_devices_cfg.php");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            //add post header
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            console.append("init db cfg at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
        } catch (Exception ex) {
            console.append(ex + "\n");
            return;
        }

        //update progress holder
        int index = 0;
        //update devices cfg
        for (String cfg : data) {
            try {
                URL obj = new URL(db + "/set_devices_cfg.php");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setConnectTimeout(5000);
                //add post header
                con.setRequestMethod("POST");

                //send post param
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(cfg);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                console.append("update db cfg(" + (++index) + "/" + data.length + ") at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");

            } catch (Exception ex) {
                console.append(ex + "\n");
                break;
            }
        }
    }

    /**
     * send data to db
     *
     * @param data
     */
    void send_to_db(String data) {
        //if adress is empty => return directly
        if (db.equals("")) {
            return;
        }

        //send data
        try {
            URL obj = new URL(db + "/save_to_db.php");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            //add post header
            con.setRequestMethod("POST");

            //send post header
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            console.append("to db => " + data.split("&")[0] + " at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
            recordLog.append(data + "\n");
        } catch (Exception ex) {
            console.append(ex + "\n");
        }
    }

    /**
     * send data to bot
     *
     * @param data
     */
    void send_to_bot(String data) {
        //if adress/data is empty => return directly
        if (bot.equals("") || data.equals("[]")) {
            return;
        }

    }

    /**
     * send data to firebase
     *
     * @param data
     */
    void send_to_firebase(String data) {
        //if adress/data is empty => return directly
        if (firebase.equals("") || data.equals("[]")) {
            return;
        }

    }

    /**
     * send data to webserver
     *
     * @param data
     */
    void send_to_webservser(String data) {
        //if adress is empty => return directly
        if (webserver.equals("")) {
            return;
        }

        //work thread
        try {
            URL obj = new URL(webserver + "/commit.php");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            //add post header
            con.setRequestMethod("POST");

            //send post header
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            console.append("to websever => " + data.split("&")[0] + " at " + LocalDateTime.now() + " wtih response code=" + responseCode + "\n");
            recordLog.append(data + "\n");
        } catch (Exception ex) {
            console.append(ex + "\n");
        }
    }
}
