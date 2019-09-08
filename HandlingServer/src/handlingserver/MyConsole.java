/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author 70136
 */
public class MyConsole extends JTextArea{
    File file;
    PrintWriter printWriter;
    int line=0;
    int limit=1000;
    public MyConsole() {
        DefaultCaret caret = (DefaultCaret)getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    
    
    @Override
    public synchronized void append(String str) {
        super.append(str); //To change body of generated methods, choose Tools | Templates.
        printWriter.append(str);
        line++;
        if(line>=limit){
            stopLog();
            startLog(LocalDateTime.now().toString());
            line=0;
            clear();
        }
    }
    synchronized void startLog(String datetime){
        clear();
        File file=new File("console "+datetime.replaceAll(":","-")+".txt");
        try {
            printWriter=new PrintWriter(file);
            printWriter.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    synchronized void stopLog(){
        printWriter.flush();
        printWriter.close();
        System.gc();
    }
    void clear(){
        this.setText(null);
    }
}
