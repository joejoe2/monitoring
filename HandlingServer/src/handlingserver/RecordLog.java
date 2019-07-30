/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author 70136
 */
public class RecordLog {
     File file;
     PrintWriter printWriter;
    public RecordLog() {
    }
    public void append(String str) {
        printWriter.append(str);  
    }
    void startLog(String datetime){
        File file=new File("record "+datetime.replaceAll(":","-")+".txt");
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
