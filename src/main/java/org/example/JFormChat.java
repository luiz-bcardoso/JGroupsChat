package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JFormChat {
    private JButton dizOiButton;
    private JPanel panelMain;

    public JFormChat() {
        dizOiButton.addActionListener(e -> dizOi());
    }

    public void dizOi(){
        JOptionPane.showMessageDialog(null, "Oi!");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JFormChat");
        frame.setContentPane(new JFormChat().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // Center GUI location and sets visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
