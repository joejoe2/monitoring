/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author 70136
 */
public class AnalyzeUnit {

    JTextArea console;

    CommitUnit commitUnit;
    ArrayList<DataWindow> windows;

    /**
     * init analyze unit
     * @param windows pre-defined configs
     * @param console MyConsole obj
     */
    public AnalyzeUnit(ArrayList<DataWindow> windows, MyConsole console) {
        this.console = console;
        this.windows = windows;

    }

    /**
     * bind commitUnit to Processor
     * @param commitUnit next pipeline component
     */
    public void bind(CommitUnit commitUnit) {
        this.commitUnit = commitUnit;
        console.append("AnalyzeUnit start at " + LocalDateTime.now() + "\n");
    }

    /**
     * input process data and pass to next pipeline component
     * @param data input
     */
    public void add(String data) {

            String dataIN = data;
            String ID = dataIN.substring(dataIN.indexOf("devicesid=") + 10, dataIN.indexOf("&status"));
            String msg = "";
            for (DataWindow window : windows) {
                if (window.devicesID.equals(ID)) {//if match defined
                    //get evaluated result
                    String result = window.evaluate(dataIN.substring(dataIN.indexOf("obj=") + 4), !dataIN.contains("unavailable"));
                    msg = result.split("&")[1].split("=")[1];
                    dataIN = dataIN.substring(0, dataIN.indexOf("obj=") + 4) + result.split("&")[0];

                    //send data , msg
                    commitUnit.add(dataIN, msg);
                    break;
                }
            }

    }
}
