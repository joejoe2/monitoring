/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.time.LocalDateTime;
import javax.swing.JTextArea;

/**
 *
 * @author 70136
 */
public class Processor {

    JTextArea console;

    AnalyzeUnit analyzeUnit;

    public Processor(JTextArea console) {
        this.console = console;

    }

    void bind(AnalyzeUnit analyzeUnit) {
        this.analyzeUnit = analyzeUnit;
        console.append("Processor start at " + LocalDateTime.now() + "\n");
    }

    void add(String data) {
        //new Thread(() -> {

            String dataIN = data;

            //process dataIN...
            //console.append("process data => "+dataIN+" at "+LocalDateTime.now()+"\n");
            //add to analyze unit...
            analyzeUnit.add(dataIN);

        //}).start();
    }
}
