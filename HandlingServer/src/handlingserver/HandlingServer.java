/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author 70136
 */
public class HandlingServer extends JFrame {

    String analyzeAddr;
    String webserver = "http://showdata.nctu.me/webserver";
    String db = "http://showdata.nctu.me/db";
    String bot = "";
    String firebase = "";
    JLabel webserver_label, db_label, bot_label, firebase_label;
    JTextField input_websever;
    JTextField input_db;
    JTextField input_bot;
    JTextField input_firebase;
    JButton startBtn;
    MyConsole console;
    RecordLog recordLog;

    Receiver receiver;
    Processor processor;
    AnalyzeUnit analyzeUnit;
    CommitUnit commitUnit;

    String[] defaultStr;
    ArrayList<DataWindow> windows;
    boolean isruning = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        HandlingServer handlingServer = new HandlingServer();
    }

    public HandlingServer() {

        //
        defaultStr = new String[0];

        //component
        webserver_label = new JLabel("webserver address : ");
        webserver_label.setSize(150, 25);
        webserver_label.setLocation(0, 20);
        webserver_label.setFont(new Font("", 0, 15));
        this.add(webserver_label);

        db_label = new JLabel("db address : ");
        db_label.setSize(150, 25);
        db_label.setLocation(0, 60);
        db_label.setFont(new Font("", 0, 15));
        this.add(db_label);

        bot_label = new JLabel("bot address : ");
        bot_label.setSize(150, 25);
        bot_label.setLocation(0, 110);
        bot_label.setFont(new Font("", 0, 15));
        this.add(bot_label);

        firebase_label = new JLabel("firebase address : ");
        firebase_label.setSize(150, 25);
        firebase_label.setLocation(0, 160);
        firebase_label.setFont(new Font("", 0, 15));
        this.add(firebase_label);

        input_websever = new JTextField();
        input_websever.setSize(300, 50);
        input_websever.setLocation(150, 0);
        input_websever.setText(webserver);
        this.add(input_websever);

        input_db = new JTextField();
        input_db.setSize(300, 50);
        input_db.setLocation(150, 50);
        input_db.setText(db);
        this.add(input_db);

        input_bot = new JTextField();
        input_bot.setSize(300, 50);
        input_bot.setLocation(150, 100);
        input_bot.setText(bot);
        this.add(input_bot);

        input_firebase = new JTextField();
        input_firebase.setSize(300, 50);
        input_firebase.setLocation(150, 150);
        input_firebase.setText(firebase);
        this.add(input_firebase);

        startBtn = new JButton("start server");
        startBtn.setSize(150, 50);
        startBtn.setLocation(500, 0);
        startBtn.addActionListener((e) -> {
            if (!isruning) {//if is not runing
                
                //read setting
                readSetting();

                //get address
                webserver = input_websever.getText().trim();
                db = input_db.getText().trim();

                //set running flag
                isruning = true;

                //start log
                console.startLog(LocalDateTime.now().toString());
                recordLog = new RecordLog();
                recordLog.startLog(LocalDateTime.now().toString());

                //initialize component
                receiver = new Receiver(console, defaultStr);
                processor = new Processor(console);
                analyzeUnit = new AnalyzeUnit(windows, console);
                commitUnit = new CommitUnit(console, recordLog);

                //bind component
                receiver.bind(processor);
                processor.bind(analyzeUnit);
                analyzeUnit.bind(commitUnit);
                commitUnit.bind(webserver, db, bot, firebase, defaultStr);

                //change start/atop button text
                startBtn.setText("stop server");
            } else {//if is runing

                isruning = false;//clear start/stop button flag
                receiver.stop();//stop coponent
                startBtn.setText("start server");//change start/atop button text
                console.append("server try to stop at " + LocalDateTime.now() + "\n");
                console.stopLog();
                recordLog.stopLog();
            }
        });
        this.add(startBtn);

        console = new MyConsole(1000, 50);
        console.setSize(680, 500);
        console.setAutoscrolls(true);
        console.setLineWrap(true);
        console.setEnabled(false);
        console.setFont(new Font("", 0, 15));
        console.setForeground(Color.WHITE);
        console.setBackground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setSize(console.getSize());
        scrollPane.setLocation(0, 250);
        this.add(scrollPane);
        //frame
        this.setSize(700, 800);
        this.setLayout(null);
        this.setVisible(true);
        //set close event handler
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (isruning) {
                    isruning = false;
                    //stop coponent
                    receiver.stop();
                    startBtn.setText("start server");
                    console.append("server try to stop at " + LocalDateTime.now() + "\n");
                    console.stopLog();
                    recordLog.stopLog();
                }
                System.exit(0);
            }
        });
    }

    void readSetting() {

        try {
            File file = new File("setting.ini");
            Scanner scanner = new Scanner(file);
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> dw = new ArrayList<>();
            ArrayList<DataWindow> window = new ArrayList<>();
            DataWindow holder = null;
            int num = 0;
            String id = "";
            while (scanner.hasNextLine()) {
                String de = scanner.nextLine();
                if (de.startsWith("target=")) {
                    list.add(de);
                    String[] wind = new String[num];
                    wind = dw.toArray(wind);
                    holder = new DataWindow(id, num, wind);
                    window.add(holder);
                    holder = null;
                    num = 0;
                    dw.clear();
                } else if (de.startsWith("devices")) {
                    id = de.substring("devices".length());
                } else if (de.startsWith("sensor")) {
                    num++;
                    dw.add(de);
                } else {
                    dw.add(de);
                }
            }
            //set default value
            defaultStr = list.toArray(defaultStr);
            windows = window;
        } catch (Exception ex) {
            Logger.getLogger(HandlingServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
