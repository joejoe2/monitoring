/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author 70136
 */
public class MyConsole extends JTextArea {

    File file;
    PrintWriter printWriter;
    int log = 0;
    int loglimit;
    int show = 0;
    int showlimit;

    public MyConsole(int loglimit, int showlimit) {
        DefaultCaret caret = (DefaultCaret) getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.loglimit = loglimit;
        this.showlimit = showlimit;
    }

    @Override
    public synchronized void append(String str) {

        printWriter.append(str);
        log++;
        show++;
        if (log >= loglimit) {
            stopLog();
            startLog(LocalDateTime.now().toString());
            log = 0;
        }
        if (show >= showlimit) {
            clear();
            show = 0;
        }
        super.append(str); //To change body of generated methods, choose Tools | Templates.
    }

    synchronized void startLog(String datetime) {
        clear();
        File file = new File("console " + datetime.replaceAll(":", "-") + ".txt");
        try {
            printWriter = new PrintWriter(file);
            printWriter.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    synchronized void stopLog() {
        printWriter.flush();
        printWriter.close();
        System.gc();
    }

    synchronized void clear() {
        this.setText(null);
        System.gc();
    }
}
