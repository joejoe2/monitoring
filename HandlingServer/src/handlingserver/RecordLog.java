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

/**
 *
 * @author 70136
 */
public class RecordLog {
     File file;
     PrintWriter printWriter;
     int line=0;
     int limit=1000;
    public RecordLog() {
    }
    public synchronized void append(String str) {
        printWriter.append(str);
        line++;
        if(line>=limit){
            stopLog();
            startLog(LocalDateTime.now().toString());
            line=0;
        }
    }
    synchronized void startLog(String datetime){
        File file=new File("record "+datetime.replaceAll(":","-")+".txt");
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
}
