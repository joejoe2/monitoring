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
public class AnalyzeUnit {

    JTextArea console;
    LinkedList<String> linkedList;
    CommitUnit commitUnit;

    public AnalyzeUnit(String addr, JTextArea console) {
        this.console = console;
        console.append("AnalyzeUnit start at " + LocalDateTime.now() + "\n");
        linkedList = new LinkedList<>();
    }

    void bind(CommitUnit commitUnit) {
        this.commitUnit = commitUnit;
    }

    synchronized void add(String data) {
        new Thread(() -> {
            linkedList.add(data);
            if (!linkedList.isEmpty()) {
                String dataIN=linkedList.pollFirst();
                //
                dataIN=dataIN.replaceAll("test","testing");
                console.append("analyze data => "+dataIN+" at "+LocalDateTime.now()+"\n");
                //
                commitUnit.add(dataIN);
                
            }
        }).start();
    }
}
