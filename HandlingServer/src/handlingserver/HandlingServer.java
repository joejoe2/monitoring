/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    String commitAddr;
    JLabel analyzeLabel, commitLabel;
    JTextField input_analyze;
    JTextField input_commit;
    JButton startBtn;
    MyConsole console;
    RecordLog recordLog;

    Receiver receiver;
    Processor processor;
    AnalyzeUnit analyzeUnit;
    CommitUnit commitUnit;

    String[] defaultStr; 
    
    boolean isruning = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new HandlingServer();
    }

    public HandlingServer() {
        //
        defaultStr=new String[0];
        readSetting();
        //component
        analyzeLabel = new JLabel("analyze address");
        analyzeLabel.setSize(150, 25);
        analyzeLabel.setLocation(0, 20);
        analyzeLabel.setFont(new Font("", 0, 15));
        this.add(analyzeLabel);

        commitLabel = new JLabel("commit address");
        commitLabel.setSize(150, 25);
        commitLabel.setLocation(0, 70);
        commitLabel.setFont(new Font("", 0, 15));
        this.add(commitLabel);

        input_analyze = new JTextField();
        input_analyze.setSize(150, 50);
        input_analyze.setLocation(150, 0);
        this.add(input_analyze);

        input_commit = new JTextField();
        input_commit.setSize(150, 50);
        input_commit.setLocation(150, 50);
        this.add(input_commit);

        startBtn = new JButton("start server");
        startBtn.setSize(150, 50);
        startBtn.setLocation(300, 0);
        startBtn.addActionListener((e) -> {
            if (!isruning) {
                analyzeAddr = input_analyze.getText();
                commitAddr = input_commit.getText();
                isruning = true;
                //start log
                console.startLog(LocalDateTime.now().toString());
                recordLog=new RecordLog();
                recordLog.startLog(LocalDateTime.now().toString());
                //initialize component
                receiver = new Receiver(console,defaultStr);
                processor = new Processor(console);
                analyzeUnit = new AnalyzeUnit(analyzeAddr, console);
                commitUnit = new CommitUnit(commitAddr, console,recordLog);

                //bind component
                receiver.bind(processor);
                processor.bind(analyzeUnit);
                analyzeUnit.bind(commitUnit);
                commitUnit.bind();
                
                startBtn.setText("stop server");
            } else {
                isruning = false;
                //stop coponent
                receiver.stop();
                startBtn.setText("start server");
                console.append("server try to stop at " + LocalDateTime.now()+"\n");
                console.stopLog();
                recordLog.stopLog();
            }
        });
        this.add(startBtn);

        console = new MyConsole();
        console.setSize(470, 350);
        console.setAutoscrolls(true);
        console.setLineWrap(true);
        console.setEnabled(false);
        console.setFont(new Font("", 0, 15));
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setSize(console.getSize());
        scrollPane.setLocation(0, 100);
        this.add(scrollPane);
        //frame
        this.setSize(500, 500);
        this.setLayout(null);
        this.setVisible(true);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if(isruning){
                isruning = false;
                //stop coponent
                receiver.stop();
                startBtn.setText("start server");
                console.append("server try to stop at " + LocalDateTime.now()+"\n");
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
            Scanner scanner=new Scanner(file);
            ArrayList<String> list=new ArrayList<>();
            while(scanner.hasNextLine()){
                list.add(scanner.nextLine());
            }
            defaultStr=list.toArray(defaultStr);
        } catch (Exception ex) {
            Logger.getLogger(HandlingServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
