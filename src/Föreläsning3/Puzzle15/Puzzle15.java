package Föreläsning3.Puzzle15;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Puzzle15 extends JFrame {
    private JPanel panel = new JPanel();
    private JPanel upper = new JPanel();
    private JButton newGameButton = new JButton("New Game");
    private JPanel buttonPanel = new JPanel();
    private JTextArea message= new JTextArea();
    private final int puzzleSize = 4;
    private ArrayList<String> puzzlelist = new ArrayList<>();

    public Puzzle15() {
        panel.setLayout(new BorderLayout());
        add(panel);

        upper.add(newGameButton, BorderLayout.CENTER);
        panel.add(upper, BorderLayout.NORTH);
        buttonPanel.setLayout(new GridLayout(puzzleSize, puzzleSize));

        Shuffler();

        panel.add(buttonPanel, BorderLayout.CENTER);
        message.setLayout(new BorderLayout());
        message.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment

//        message.setEditable(false);
//        message.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font
//        message.setMargin(new Insets(10, 10, 10, 10)); // Padding
//        message.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment

        panel.add(message, BorderLayout.SOUTH);

        newGameButton.addActionListener(e -> {
            // Clear and recreate the game
            buttonPanel.removeAll();
            Shuffler();
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void Shuffler() {
        puzzlelist.clear();
        // Add numbers 1-15 (leave one empty space for the puzzle)
        for (int i = 1; i < puzzleSize * puzzleSize; i++) {
            puzzlelist.add(String.valueOf(i));
        }

//        Collections.shuffle(puzzlelist);
        // Add empty space (represented as empty string)
        puzzlelist.add("");

        createButtonPanel();
    }

    private void createButtonPanel() {
        buttonPanel.removeAll(); // Clear existing buttons

        for (String value : puzzlelist) {
            JButton button;
            if (value.isEmpty()) {
                button = new JButton(); // Empty button
                button.setEnabled(false); // Typically the empty space is not clickable
            } else {
                button = new JButton(value);
                button.addActionListener(e -> game(button));
            }
            buttonPanel.add(button);
        }
    }

    private void game(JButton button) {
        // Get the clicked button's text and position
        String buttonText = button.getText();
        if (buttonText.isEmpty()) return; // Don't process clicks on the empty space

        // Find positions of clicked button and empty button
        int clickedIndex = -1;
        int emptyIndex = -1;

        // Search through all buttons to find positions
        Component[] buttons = buttonPanel.getComponents();
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == button) {
                clickedIndex = i;
            }
            if (((JButton) buttons[i]).getText().isEmpty()) {
                emptyIndex = i;
            }
        }

        // Check if the clicked button is adjacent to the empty space
        if (isAdjacent(clickedIndex, emptyIndex)) {
            // Swap the buttons
            puzzlelist.set(clickedIndex, "");
            puzzlelist.set(emptyIndex, buttonText);

            // Update the display
            buttonPanel.removeAll();
            createButtonPanel();
            buttonPanel.revalidate();
            buttonPanel.repaint();

            // Check if puzzle is solved
            checkCompletion();
        }
    }

    private boolean isAdjacent(int index1, int index2) {
        int row1 = index1 / puzzleSize;
        int col1 = index1 % puzzleSize;
        int row2 = index2 / puzzleSize;
        int col2 = index2 % puzzleSize;

        // Check if positions are adjacent horizontally or vertically
        return (Math.abs(row1 - row2) == 1 && col1 == col2) ||
                (Math.abs(col1 - col2) == 1 && row1 == row2);
    }

    private void checkCompletion() {
        for (int i = 0; i < puzzlelist.size() - 1; i++) {
            if (!puzzlelist.get(i).equals(String.valueOf(i + 1))) {
                return; // Puzzle not solved
            }
        }
        // If we get here, puzzle is solved
        JOptionPane.showMessageDialog(this, "Well Done! Puzzle Solved!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);

        message.setText("Well Done");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Puzzle15());
    }
}