package com.b3.gui.components;

import javax.swing.JOptionPane;

public class MessageBoxComponent {
    public static void show(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}