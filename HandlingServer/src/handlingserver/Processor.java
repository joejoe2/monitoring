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

    /**
     * init Processor unit
     * @param console MyConsole obj
     */
    public Processor(MyConsole console) {
        this.console = console;
    }

    /**
     * bind analyzeUnit to Processor
     * @param analyzeUnit next pipeline component
     */
    public void bind(AnalyzeUnit analyzeUnit) {
        this.analyzeUnit = analyzeUnit;
        console.append("Processor start at " + LocalDateTime.now() + "\n");
    }

    /**
     * input process data and pass to next pipeline component
     * @param data input
     */
    public void add(String data) {
        Pool.execute(() -> {//this thread is for all following pipeline components to work

            String dataIN = data;

            //process dataIN...
            //pass to analyze unit
            analyzeUnit.add(dataIN);

        });
    }
}
