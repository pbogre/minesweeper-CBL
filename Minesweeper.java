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
            setText("B");
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
        private int bombAmount;
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

        private void populateBombs() {
            Random random = new Random();
            int remainingBombs = this.bombAmount;

            while (remainingBombs > 0) {
                int randomColumn = random.nextInt(this.gridSize);
                int randomRow = random.nextInt(this.gridSize);
                
                if (this.cells[randomRow][randomColumn].isBomb) {
                    continue;
                }

                this.cells[randomRow][randomColumn].makeBomb();
                remainingBombs--;
            }
        }

        public Game(int gridSize, int bombAmount){
            this.cellSize = 35;
            this.gridSize = gridSize;
            this.bombAmount = bombAmount;
            this.cells = new Cell[this.gridSize][this.gridSize];

            setSize(this.gridSize * this.cellSize, this.gridSize * this.cellSize);
            setLayout(new GridLayout(this.gridSize, this.gridSize));

            for (int row = 0; row < this.gridSize; row++) {
                for (int col = 0; col < this.gridSize; col++) {
                    cells[row][col] = new Cell(row, col);
                    add(cells[row][col]);
                }
            }

            populateBombs();
        }
    }

    private class Menu extends JFrame {

        private int selectedGridSize;
        private int selectedBombAmount;
        
        private void run() {
            setTitle("Menu");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(WINDOW_SIZE, WINDOW_SIZE); // Set the frame size
            setLocationRelativeTo(null); // Center the frame on the screen
            setVisible(true);
        }

        private void stop() {
            setVisible(false);
            dispose();
        }

        private Menu() {
            // default grid size & bomb amount
            this.selectedGridSize = 16;
            this.selectedBombAmount = 80;

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

            JSlider gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 5, 35, this.selectedGridSize);
            JSlider bombAmountSlider = new JSlider(JSlider.HORIZONTAL, 0, 99, this.selectedBombAmount);

            JLabel gridSizeLabel = new JLabel("Grid size: " + gridSizeSlider.getValue());
            JLabel bombAmountLabel = new JLabel("Bomb / Cells: " + bombAmountSlider.getValue() + "% (" + this.selectedBombAmount + " total)");

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
            gridSizeSlider.setMajorTickSpacing(10);
            gridSizeSlider.setMinorTickSpacing(1);
            gridSizeSlider.setPaintTicks(true);
            gridSizeSlider.setPaintLabels(true);
            gridSizeSlider.setVisible(false);
            add(gridSizeSlider, constraints);

            constraints.gridx = 0;
            constraints.gridy = 5;
            gridSizeLabel.setVisible(false);
            add(gridSizeLabel, constraints);

            constraints.gridx = 0;
            constraints.gridy = 6;
            bombAmountSlider.setMajorTickSpacing(33);
            bombAmountSlider.setPaintTicks(true);
            bombAmountSlider.setPaintLabels(true);
            bombAmountSlider.setVisible(false);
            add(bombAmountSlider, constraints);

            constraints.gridx = 0;
            constraints.gridy = 7;
            bombAmountLabel.setVisible(false);
            add(bombAmountLabel, constraints);

            Menu self = this;
            startGameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        // Start game and stop menu
                        Game game = new Game(self.selectedGridSize, self.selectedBombAmount);
                        game.run();
                        self.stop();
                    }
            });

            // difficulty inputs event listeners
            easyDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    self.selectedGridSize = 8;
                    self.selectedBombAmount = 20;
                }
            });
            mediumDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    self.selectedGridSize = 16;
                    self.selectedBombAmount = 80;
                }
            });
            hardDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    self.selectedGridSize = 32;
                    self.selectedBombAmount = 350;
                }
            });
            customDifficultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                        gridSizeSlider.setVisible(!gridSizeSlider.isVisible());
                        gridSizeLabel.setVisible(!gridSizeLabel.isVisible());
                        bombAmountSlider.setVisible(!bombAmountSlider.isVisible());
                        bombAmountLabel.setVisible(!bombAmountLabel.isVisible());
                    }
            });
            gridSizeSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
    
                    gridSizeLabel.setText("Grid size: " + gridSizeSlider.getValue());
                    self.selectedGridSize = gridSizeSlider.getValue();

                    self.selectedBombAmount = (int)(self.selectedGridSize * selectedGridSize * (double)(bombAmountSlider.getValue() / 100.0));
                    bombAmountLabel.setText("Bombs / Cells: " + bombAmountSlider.getValue() + "% (" + self.selectedBombAmount + " total)");
                }
            });
            bombAmountSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
    
                    self.selectedBombAmount = (int)(self.selectedGridSize * selectedGridSize * (double)(bombAmountSlider.getValue() / 100.0));
                    bombAmountLabel.setText("Bombs / Cells: " + bombAmountSlider.getValue() + "% (" + self.selectedBombAmount + " total)");
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
