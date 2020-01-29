/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
    boolean enable, is_gui;

    public MyConsole(int loglimit, int showlimit, boolean is_gui) {
        this.is_gui = is_gui;
        this.loglimit = loglimit;
        this.showlimit = showlimit;

        if (is_gui) {
            DefaultCaret caret = (DefaultCaret) getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            caret.setVisible(true);
            caret.setSelectionVisible(true);
            this.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    caret.setSelectionVisible(true);
                    caret.setVisible(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    caret.setSelectionVisible(true);
                    caret.setVisible(true);
                }
            });

            this.setAutoscrolls(true);
            this.setLineWrap(true);
            this.setEditable(false);
            this.setFont(new Font("", 0, 15));
            this.setForeground(Color.WHITE);
            this.setBackground(Color.BLACK);
            this.setCaretColor(Color.WHITE);
            this.setSelectionColor(Color.WHITE);
            this.setSelectedTextColor(Color.BLACK);
        }

    }

    @Override
    public synchronized void append(String str) {
        if (enable) {
            printWriter.append(str);
        }
        System.out.println(str);
        

        log++;
        show++;
        if (log >= loglimit) {
            stopLog();
            startLog(LocalDateTime.now().toString(), this.enable);
            log = 0;
        }
        if (show >= showlimit) {
            clear();
            show = 0;
        }

        if (is_gui) {
            super.append(str);
            getCaret().setDot(this.getText().length());
        }

    }

    synchronized void startLog(String datetime, boolean enable) {
        this.enable = enable;
        if (!this.enable) {
            return;
        }
        clear();
        log = 0;
        show = 0;
        File file = new File("console " + datetime.replaceAll(":", "-") + ".txt");
        try {
            printWriter = new PrintWriter(file);
            printWriter.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    synchronized void stopLog() {
        if (!enable) {
            return;
        }
        printWriter.flush();
        printWriter.close();
        System.gc();
    }

    synchronized void clear() {
        if (is_gui) {
            this.setText("");
        }
        System.gc();
    }
}
