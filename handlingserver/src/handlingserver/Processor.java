/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.time.LocalDateTime;
import java.util.LinkedList;
import javax.swing.JTextArea;

/**
 *
 * @author 70136
 */
public class Processor {

    JTextArea console;
    LinkedList<String> linkedList;
    AnalyzeUnit analyzeUnit;

    public Processor(JTextArea console) {
        this.console = console;
        console.append("Processor start at " + LocalDateTime.now() + "\n");
        linkedList = new LinkedList<>();
    }

    void bind(AnalyzeUnit analyzeUnit) {
        this.analyzeUnit = analyzeUnit;
    }

    synchronized void add(String data) {
        new Thread(() -> {
            linkedList.add(data);
            if (!linkedList.isEmpty()) {
                String dataIN = linkedList.pollFirst();
                
                //process dataIN...
                
                console.append("process data => "+dataIN+" at "+LocalDateTime.now()+"\n");
                //add to analyze unit...
                analyzeUnit.add(dataIN);
            }
        }).start();
    }
}
