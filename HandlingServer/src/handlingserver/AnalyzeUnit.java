/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    ArrayList<DataWindow> windows;
    
    public AnalyzeUnit(ArrayList<DataWindow> windows, JTextArea console) {
        this.console = console;
        this.windows=windows;
        linkedList = new LinkedList<>();
    }

    void bind(CommitUnit commitUnit) {
        this.commitUnit = commitUnit;
        console.append("AnalyzeUnit start at " + LocalDateTime.now() + "\n");
    }

    synchronized void add(String data) {
        new Thread(() -> {
            linkedList.add(data);
            if (!linkedList.isEmpty()) {
                String dataIN=linkedList.pollFirst();
                //
                String ID=dataIN.substring(dataIN.indexOf("devicesid=")+10, dataIN.indexOf("&status"));
                if(!dataIN.contains("unavailable")){
                for (DataWindow window : windows) {
                    if(window.devicesID.equals(ID)){
                        dataIN=dataIN.substring(0,dataIN.indexOf("obj=")+4)+window.evaluate(dataIN.substring(dataIN.indexOf("obj=")+4));
                        break;
                    }
                }
                }
                //console.append("analyze data => "+dataIN+" at "+LocalDateTime.now()+"\n");
                //
                commitUnit.add(dataIN);
                
            }
        }).start();
    }
}
