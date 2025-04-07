package Föreläsning3.TextEditor;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TextEditor extends JFrame implements ActionListener {
    JPanel panel = new JPanel();

    private JButton Öppna = new JButton("Öppna");
    private JButton Spara = new JButton("Spara");
    private JButton Skrive_ut = new JButton("Skrive ut");
    private JButton Avsluta = new JButton("Avsluta");
    private JLabel Filenamn = new JLabel("Filenamn");
    private JTextField filenamn_textField = new JTextField(20);
    private JTextArea TextArea = new JTextArea(20,60);
    private JScrollPane sp = new JScrollPane(TextArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    public TextEditor() {
        panel.setLayout(new BorderLayout());
        add(panel);
//        setIconImage("C:\\Users\\samue\\Desktop\\github-logo2.png");

        JPanel southPanel = new JPanel();
//        southPanel.setLayout(new GridLayout(1,6));
        southPanel.setLayout(new FlowLayout());
        southPanel.add(Filenamn);
        southPanel.add(filenamn_textField);
        southPanel.add(Öppna);
        southPanel.add(Spara);
        southPanel.add(Skrive_ut);
        southPanel.add(Avsluta);
        panel.add(southPanel, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        filenamn_textField.addActionListener(this);
        Öppna.addActionListener(this);
        Spara.addActionListener(this);
        Skrive_ut.addActionListener(this);
        Avsluta.addActionListener(this);


        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == Öppna)
            readFile(filenamn_textField.getText());
        else if (e.getSource() == Spara) 
            saveFile(filenamn_textField.getText());
        else if (e.getSource() == Skrive_ut) {
            try {
                TextArea.print();  // skriver ut texten, kan ge exception
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == Avsluta)
                System.exit(0);

    }

    private void saveFile(String filename) {

        try (FileWriter w = new FileWriter(filename)){
            TextArea.write(w);
            JOptionPane.showMessageDialog(null, "Data sparades");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(String filename) {
        try (FileReader r = new FileReader(filename);){
            TextArea.read(r, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new TextEditor();
    }
}

