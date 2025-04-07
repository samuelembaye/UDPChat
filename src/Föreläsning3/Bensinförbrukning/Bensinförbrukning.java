package Föreläsning3.Bensinförbrukning;

import SwingProject.JFrameDemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Integer.parseInt;

public class Bensinförbrukning extends javax.swing.JFrame implements ActionListener {
    JPanel panel = new JPanel();
    JLabel Label1 = new JLabel("Ange Mätarställnong:");
    JLabel Label2 = new JLabel("Ange Mätarställnong för ett År sedan:");
    JLabel Label3 = new JLabel("Ange Antal liter förbrukad Bensin :");
    JTextField TextField1 = new JTextField();
    JTextField TextField2 = new JTextField();
    JTextField TextField3 = new JTextField();

    JLabel Label4 = new JLabel("Antal Körda mill:");
    JLabel Label5 = new JLabel("Antal liter Bensin:");
    JLabel Label6 = new JLabel("Förbrukad per mill:");

    public Bensinförbrukning() throws HeadlessException {
        panel.setLayout(new BorderLayout());
        add(panel);
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(3,2));
        northPanel.add(Label1);
        northPanel.add(TextField1);

        northPanel.add(Label2);
        northPanel.add(TextField2);
        northPanel.add(Label3);
        northPanel.add(TextField3);


        TextField3.addActionListener(this);

        panel.add(northPanel,BorderLayout.NORTH);
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1,3));
//        southPanel.setLayout(new );
        southPanel.add(Label4);
        southPanel.add(Label5);
        southPanel.add(Label6);
        panel.add(southPanel, BorderLayout.SOUTH);


//        setSize(400, 400);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == TextField3) {
            int nu =  parseInt(TextField1.getText());
            int last = parseInt(TextField2.getText());
            double bensin = parseInt(TextField3.getText());
            double förbrukad = bensin /(double)(nu - last);
            Label4.setText("Antal Körda mill: " + (nu - last));
            Label5.setText("Antal liter: " + bensin );
            Label6.setText(String.format("Förbrukad per mill: %1.2f ", förbrukad));

        }
    }

    public static void main(String[] args) {
        new Bensinförbrukning();
    }
}
