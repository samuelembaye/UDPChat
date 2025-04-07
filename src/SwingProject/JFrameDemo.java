package SwingProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JFrameDemo extends JFrame  implements ActionListener {


    JPanel panel = new JPanel();
    JLabel label = new JLabel("Plants");
    JLabel label0 = new JLabel("Solar System");

    JButton button = new JButton("OK");
    JButton button1 = new JButton("Earth");
    JButton button2 = new JButton("Mars");
    JButton button3 = new JButton("Jupiter");
    JButton button4 = new JButton("NEXT");

    public JFrameDemo() {
        panel.setLayout(new BorderLayout());
        add(panel);

        panel.add(button1, BorderLayout.WEST);
        panel.add(button2, BorderLayout.EAST);
        panel.add(button3, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.add(button4);
        southPanel.add(button);
        panel.add(southPanel, BorderLayout.SOUTH);

        // add 2 label in NORTH
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.add(label0);
        northPanel.add(label); // Add Venus button to north panel
        panel.add(northPanel, BorderLayout.NORTH);


        button4.addActionListener( this);
        button.addActionListener( this);

//        setSize(400, 400);
        pack();
        setLocationRelativeTo(null);
//        setLocation(200,100);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button4) {
            if(button4.getText().equals("NEXT"))
                button4.setText("PREVIOUS");
            else
                button4.setText("NEXT");

        } else if(e.getSource() == button) {
            if(button.getText().equals("OK"))
                button.setText("DONE");
            else
                button.setText("OK");
        }

    }

    public static void main(String[] args) {
         new JFrameDemo();
    }
}
