/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author 70136
 */
public class HandlingServer extends JFrame {

    //addresses
    String analyzeAddr;
    String webserver = "https://showdata.nctu.me/webserver";
    String db = "https://showdata.nctu.me/db";
    String bot = "http://joejoe2bot.herokuapp.com/kkgmsmepoa54fd9rew2da/2n11td", botkey = "a43fdfvvpefd55";
    String firebase = "AAAAymMbbkU:APA91bFwBX8tIvdPm2Q6k44gveOD-Dl6gxjS0YpJQNCB8CegNBRQaJexWczwGdphQ_2sRyrR8LtisLO3dL8zXGFVEVc_V8xoFnclV9Icf3OXRDeVf2h_yxZ9dap2kZusG63LwEg_W6IK";

    //tips label
    JLabel webserver_label, db_label, bot_label, firebase_label;

    //input fields of addresses
    JTextField input_websever;
    JTextField input_db;
    JTextField input_bot;
    JTextField input_firebase;

    //start(switch) button
    JButton startBtn;

    //enabled check box
    JCheckBox enable_logfile_box, enable_webserver_box, enable_db_box, enable_bot_box, enable_firebase_box;

    //is running flag for switch button
    boolean isruning = false;

    //logging unit
    MyConsole console;
    RecordLog recordLog;

    //pipeline unit
    Receiver receiver;
    Processor processor;
    AnalyzeUnit analyzeUnit;
    CommitUnit commitUnit;

    //default value array
    String[] defaultStr;

    //analyze list
    ArrayList<DataWindow> windows;

    //font
    Font general_font = new Font("", 0, 15);

    /**
     * start main program
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String w="",d="",b="",f="";
        boolean is_gui=true;
        
        if(args.length==0)
            is_gui=true;
        else if("-g".equals(args[0]))
            is_gui=true;
        else if("-c".equals(args[0]))
            is_gui=false;
        
        for(int i=1;i<args.length;i++){
            if("-w".equals(args[i])){
                w=args[i+1];
            }else if("-d".equals(args[i])){
                d=args[i+1];
            }else if("-b".equals(args[i])){
                b=args[i+1];
            }else if("-f".equals(args[i])){
                f=args[i+1];
            }
        }
        
        // create server obj
        HandlingServer handlingServer = new HandlingServer();
        
        if(is_gui){
            handlingServer.start_with_gui();
        }else{
            handlingServer.start_without_gui(w, d, b, f);
        }

    }

    /**
     * init keystore and trust store
     *
     */
    private void init_stores() {

        System.setProperty("javax.net.ssl.keyStore", "sslserverkeys");
        System.setProperty("javax.net.ssl.keyStorePassword", "a43235167");

        System.setProperty("javax.net.ssl.trustStore", "sslservertrust");
        System.setProperty("javax.net.ssl.trustStorePassword", "a43235167");

    }

    public void start_without_gui(String webserver, String db, String bot, String firebase) {
        //init certs
        init_stores();

        //init default value array
        defaultStr = new String[0];

        //init and setup logging unit
        console = new MyConsole(1000, 50, false);
        recordLog = new RecordLog();

        //set close event handler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isruning) {//if still running => stop the server
                //clear running falg
                isruning = false;

                //stop coponent
                receiver.stop();

                //finish logging
                console.append("server try to stop at " + LocalDateTime.now() + "\n");
                console.stopLog();
                recordLog.stopLog();
                
                System.out.println("exit");
            }
        }));

        //read setting
        readSetting();

        //set running flag
        isruning = true;

        //start logging units
        console.startLog(LocalDateTime.now().toString(), true);
        recordLog.startLog(LocalDateTime.now().toString(), true);

        //initialize pipeline component
        receiver = new Receiver(console, defaultStr);
        processor = new Processor(console);
        analyzeUnit = new AnalyzeUnit(windows, console);
        commitUnit = new CommitUnit(console, recordLog);

        //bind components
        receiver.bind(processor);
        processor.bind(analyzeUnit);
        analyzeUnit.bind(commitUnit);
        commitUnit.bind(webserver, db, bot, botkey, firebase, defaultStr);
    }

    public void start_with_gui() {
        //init certs
        init_stores();

        //init default value array
        defaultStr = new String[0];

        //set up webserver label
        webserver_label = new JLabel("webserver address : ");
        webserver_label.setSize(150, 25);
        webserver_label.setLocation(0, 20);
        webserver_label.setFont(general_font);
        this.add(webserver_label);

        //set up db label
        db_label = new JLabel("db address : ");
        db_label.setSize(150, 25);
        db_label.setLocation(0, 60);
        db_label.setFont(general_font);
        this.add(db_label);

        //set up bot label
        bot_label = new JLabel("bot address : ");
        bot_label.setSize(150, 25);
        bot_label.setLocation(0, 110);
        bot_label.setFont(general_font);
        this.add(bot_label);

        //set up firebase label
        firebase_label = new JLabel("firebase key : ");
        firebase_label.setSize(150, 25);
        firebase_label.setLocation(0, 160);
        firebase_label.setFont(general_font);
        this.add(firebase_label);

        //set up webserver input
        input_websever = new JTextField();
        input_websever.setSize(300, 50);
        input_websever.setLocation(150, 0);
        input_websever.setText(webserver);
        this.add(input_websever);

        //set up db input
        input_db = new JTextField();
        input_db.setSize(300, 50);
        input_db.setLocation(150, 50);
        input_db.setText(db);
        this.add(input_db);

        //set up bot input
        input_bot = new JTextField();
        input_bot.setSize(300, 50);
        input_bot.setLocation(150, 100);
        input_bot.setText(bot);
        this.add(input_bot);

        //set up firebase input
        input_firebase = new JTextField();
        input_firebase.setSize(300, 50);
        input_firebase.setLocation(150, 150);
        input_firebase.setText(firebase);
        this.add(input_firebase);

        //setup enabled ceck box
        enable_webserver_box = new JCheckBox("");
        enable_webserver_box.setLocation(450, 0);
        enable_webserver_box.setSize(50, 50);
        this.add(enable_webserver_box);
        enable_webserver_box.setSelected(true);
        enable_db_box = new JCheckBox("");
        enable_db_box.setLocation(450, 50);
        enable_db_box.setSize(50, 50);
        this.add(enable_db_box);
        enable_db_box.setSelected(true);
        enable_bot_box = new JCheckBox("");
        enable_bot_box.setLocation(450, 100);
        enable_bot_box.setSize(50, 50);
        this.add(enable_bot_box);
        enable_bot_box.setSelected(true);
        enable_firebase_box = new JCheckBox("");
        enable_firebase_box.setLocation(450, 150);
        enable_firebase_box.setSize(50, 50);
        this.add(enable_firebase_box);
        enable_firebase_box.setSelected(true);
        enable_logfile_box = new JCheckBox("log to file");
        enable_logfile_box.setLocation(550, 200);
        enable_logfile_box.setSize(150, 50);
        this.add(enable_logfile_box);
        enable_logfile_box.setSelected(true);

        //set up startBtn
        startBtn = new JButton("start server");
        startBtn.setSize(150, 50);
        startBtn.setLocation(500, 0);
        startBtn.addActionListener((e) -> {//switch to running or stopped
            switch_state();
        });
        this.add(startBtn);

        //init and setup logging unit
        console = new MyConsole(1000, 50, true);
        console.setSize(680, 500);
        recordLog = new RecordLog();
        //scroll bar of MyConsole logging unit view
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setSize(console.getSize());
        scrollPane.setLocation(0, 250);
        this.add(scrollPane);

        //set up window
        this.setSize(700, 800);
        this.setLayout(null);
        this.setVisible(true);

        //set close event handler
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {//when window closed
                if (isruning) {//if still running => stop the server
                    //clear running falg
                    isruning = false;

                    //stop coponent
                    receiver.stop();

                    //change start/atop button text
                    startBtn.setText("start server");

                    //finish logging
                    console.append("server try to stop at " + LocalDateTime.now() + "\n");
                    console.stopLog();
                    recordLog.stopLog();
                }

                //exit porgram
                System.exit(0);
            }
        });
    }

    /**
     * create/setup a sever obj
     */
    public HandlingServer() {

    }

    /**
     * if isfreeze=true diable all checkbox, else enable
     *
     * @param isfreeze want to freeze all checkbox or not
     */
    void checkbox_st(boolean isfreeze) {
        if (isfreeze) {
            enable_webserver_box.setEnabled(false);
            enable_db_box.setEnabled(false);
            enable_bot_box.setEnabled(false);
            enable_firebase_box.setEnabled(false);
            enable_logfile_box.setEnabled(false);
        } else {
            enable_webserver_box.setEnabled(true);
            enable_db_box.setEnabled(true);
            enable_bot_box.setEnabled(true);
            enable_firebase_box.setEnabled(true);
            enable_logfile_box.setEnabled(true);
        }
    }

    /**
     * read setting.ini in root directory of this program and get vlues to
     * defaultStr and analyze list
     */
    void readSetting() {
        //read setting.ini
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

    /**
     * switch to running or stopped
     */
    void switch_state() {
        if (!isruning) {//if is not runing => run
            //freeze check box
            checkbox_st(isruning);

            //read setting
            readSetting();

            //get address from input fields
            webserver = enable_webserver_box.isSelected() ? input_websever.getText().trim() : "";
            db = enable_db_box.isSelected() ? input_db.getText().trim() : "";
            bot = enable_bot_box.isSelected() ? input_bot.getText().trim() : "";
            firebase = enable_firebase_box.isSelected() ? input_firebase.getText().trim() : "";

            //set running flag
            isruning = true;

            //start logging units
            console.startLog(LocalDateTime.now().toString(), enable_logfile_box.isSelected());
            recordLog.startLog(LocalDateTime.now().toString(), enable_logfile_box.isSelected());

            //initialize pipeline component
            receiver = new Receiver(console, defaultStr);
            processor = new Processor(console);
            analyzeUnit = new AnalyzeUnit(windows, console);
            commitUnit = new CommitUnit(console, recordLog);

            //bind components
            receiver.bind(processor);
            processor.bind(analyzeUnit);
            analyzeUnit.bind(commitUnit);
            commitUnit.bind(webserver, db, bot, botkey, firebase, defaultStr);

            //change start/stop button text
            startBtn.setText("stop server");

        } else {//if is runing => stop
            //clear running flag
            isruning = false;

            //stop coponents
            receiver.stop();

            //change start/atop button text
            startBtn.setText("start server");

            //finish logging
            console.append("server try to stop at " + LocalDateTime.now() + "\n");
            console.stopLog();
            recordLog.stopLog();

            //release check box
            checkbox_st(isruning);
        }
    }

}
