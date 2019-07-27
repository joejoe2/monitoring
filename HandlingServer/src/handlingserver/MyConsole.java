/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author 70136
 */
public class MyConsole extends JTextArea{
    File file;
    PrintWriter printWriter;

    public MyConsole() {
        DefaultCaret caret = (DefaultCaret)getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    
    
    @Override
    public void append(String str) {
        super.append(str); //To change body of generated methods, choose Tools | Templates.
        printWriter.append(str);
        
    }
    void startLog(String datetime){
        File file=new File(datetime.replaceAll(":","-")+".txt");
        try {
            printWriter=new PrintWriter(file);
            printWriter.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    void stopLog(){
        printWriter.flush();
        printWriter.close();
        System.gc();
    }
}
