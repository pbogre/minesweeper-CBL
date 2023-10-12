import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Minesweeper {

    // The size in pixels for the frame.
    private static final int WINDOW_SIZE = 500;

    private class Cell extends JButton {
        private final int row;
        private final int col;
        private int neighboringBombs;
        private boolean isBomb;
        private boolean isRevealed;

        void makeBomb() {
            this.isBomb = true;
        }

        void setNeighboringBombs(int neighboringBombs) {
            this.neighboringBombs = neighboringBombs;
        }

        void reveal() {
            this.isRevealed = true;
            // do button stuff...
        }

        Cell(int row, int col) {
            this.row = row;
            this.col = col;

            setText("");

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // cell is clicked... 
                }
            });
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Cell cell = (Cell) obj;
            return row == cell.row &&
                   col == cell.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }
  
    private class Game extends JFrame{
        private int gridSize;
        private int cellSize;
        private Cell[][] cells;

        private void stop() {
            setVisible(false);
            dispose();
        }

        private void run() {
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        public Game(int gridSize){
            this.cellSize = 35;
            this.gridSize = gridSize;
            this.cells = new Cell[this.gridSize][this.gridSize];

            setSize(this.gridSize * this.cellSize, this.gridSize * this.cellSize);

            setLayout(new GridLayout(this.gridSize, this.gridSize));

            for (int row = 0; row < this.gridSize; row++) {
                for (int col = 0; col < this.gridSize; col++) {
                    cells[row][col] = new Cell(row, col);
                    add(cells[row][col]);
                }
            }
        }
    }

    private class Menu extends JFrame {

        private int selectedGridSize;
        
        private void run() {
            setTitle("Menu");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(WINDOW_SIZE, WINDOW_SIZE); // Set the frame size
            setLocationRelativeTo(null); // Center the frame on the screen
                                         //
            setVisible(true);
        }

        private void stop() {
            setVisible(false);
            dispose();
        }

        private Menu() {
            // default grid size
            this.selectedGridSize = 16;

            setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);

            // Add a big label for the title
            JLabel titleLabel = new JLabel("Minesweeper");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 40));

            JLabel selectDifficultyLabel = new JLabel("Select Difficulty");
            selectDifficultyLabel.setFont(new Font("Arial", Font.ITALIC, 18));

            JButton startGameButton = new JButton("Start Game");

            JButton easyDifficultyButton = new JButton("Easy");
            JButton mediumDifficultyButton = new JButton("Medium");
            JButton hardDifficultyButton = new JButton("Hard");
            JButton customDifficultyButton = new JButton("Custom");

            JPanel difficultyPanel = new JPanel();

            JSlider difficultySlider = new JSlider(JSlider.HORIZONTAL, 5, 35, this.selectedGridSize);

            JLabel difficultyValueLabel = new JLabel("Grid size: " + difficultySlider.getValue());

            constraints.gridx = 0;
            constraints.gridy = 0;
            add(titleLabel, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            add(startGameButton, constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            add(selectDifficultyLabel, constraints);
            
            difficultyPanel.add(easyDifficultyButton);
            difficultyPanel.add(mediumDifficultyButton);
            difficultyPanel.add(hardDifficultyButton);
            difficultyPanel.add(customDifficultyButton);

            constraints.gridx = 0;
            constraints.gridy = 3;
            add(difficultyPanel, constraints);

            constraints.gridx = 0;
            constraints.gridy = 4;
            difficultySlider.setMajorTickSpacing(10);
            difficultySlider.setMinorTickSpacing(1);
            difficultySlider.setPaintTicks(true);
            difficultySlider.setPaintLabels(true);
            difficultySlider.setVisible(false);
            add(difficultySlider, constraints);

            constraints.gridx = 0;
            constraints.gridy = 5;
            difficultyValueLabel.setVisible(false);
            add(difficultyValueLabel, constraints);

            Menu self = this;
            startGameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        // Start game and stop menu
                        Game game = new Game(self.selectedGridSize);
                        game.run();
                        self.stop();
                    }
            });

            // difficulty inputs event listeners
            easyDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    self.selectedGridSize = 8;
                }
            });
            mediumDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    self.selectedGridSize = 16;
                }
            });
            hardDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    self.selectedGridSize = 32;
                }
            });
            customDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                        difficultySlider.setVisible(!difficultySlider.isVisible());
                        difficultyValueLabel.setVisible(!difficultyValueLabel.isVisible());
                    }
            });
            difficultySlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
    
                    difficultyValueLabel.setText("Grid size: " + difficultySlider.getValue());
                    self.selectedGridSize = difficultySlider.getValue();
                }
            });
        }
    }

    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper();
        Minesweeper.Menu menu = minesweeper.new Menu();
        menu.run();
    }
}
