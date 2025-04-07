package Föreläsning4.Threading;

import javax.swing.*;
import java.awt.*;

public class MedicinePop extends JFrame {
    JPanel panel = new JPanel();
    JButton button = new JButton("DONE");
//    String medicine;


    public MedicinePop(String medicine) {
        add(panel);
        JTextField medicineName =  new JTextField(medicine);
        panel.setLayout( new BorderLayout());
        panel.add(medicineName, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        button.addActionListener(e -> dispose());

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
}
