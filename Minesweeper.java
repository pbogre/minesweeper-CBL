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

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // game lost
                    System.out.println("You lose!");
                }
            });
        }

        void setNeighboringBombs(int neighboringBombs) {
            this.neighboringBombs = neighboringBombs;
            setText(String.valueOf(this.neighboringBombs));
        }
        void reveal() {
            this.isRevealed = true;
            /*setIcon(new ImageIcon(new ImageIcon(
                "C:/TUe/Homework/MineSweeperCBL/minesweeper-CBL/res/Minesweeper_1png.png").getImage()
                .getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)));*/
            // do button stuff...
        }

        Cell(int row, int col) {
            this.row = row;
            this.col = col;

            setText("");
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Customize the appearance of the button to resemble a Minesweeper tile
            if (isRevealed) {
                setBackground(Color.GRAY);
                setBorder(BorderFactory.createLoweredBevelBorder());
            } 
            else {
                setBackground(Color.LIGHT_GRAY);
                setBorder(BorderFactory.createRaisedBevelBorder());
            }
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

        private void computeNeighboringBombs(Cell cell) {

            int neighboringBombs = 0;

            for(int y = cell.row - 1; y <= cell.row + 1; y++) {
                for(int x = cell.col - 1; x <= cell.col + 1; x++) {
                    // skip out of bounds
                    if(y < 0 || x < 0 || y >= this.gridSize || x >= this.gridSize) {
                        continue;
                    }
                    // skip if cell is revealed
                    if(this.cells[y][x].isRevealed) {
                        continue;
                    }

                    if (this.cells[y][x].isBomb) {
                        neighboringBombs++;
                    }
                }
            }

            cell.setNeighboringBombs(neighboringBombs);

            // If this cell has no adjacent mines, recursively reveal its neighbors
            if (neighboringBombs == 0) {               
                // Define relative positions of neighboring cells
                int[] dy = {-1, -1, -1,  0,  1,  1,  1,  0};
                int[] dx = {-1,  0,  1,  1,  1,  0, -1, -1};
                
                for (int i = 0; i < 8; i++) {
                    int currentRow = cell.row + dy[i];
                    int currentCol = cell.col + dx[i];

                    // skip out of bounds
                    if (currentRow < 0 || currentCol < 0 || currentRow >= this.gridSize || currentCol >= this.gridSize) {
                        continue;
                    }

                    Cell currentCell = this.cells[currentRow][currentCol];

                    // skip if cell is revealed
                    if (currentCell.isRevealed) {
                        continue;
                    }

                    currentCell.reveal();
                    computeNeighboringBombs(this.cells[currentRow][currentCol]);
                }
            }
        }

        public Game(int gridSize, int bombAmount){
            this.cellSize = 35;
            this.gridSize = gridSize;
            this.bombAmount = bombAmount;
            this.cells = new Cell[this.gridSize][this.gridSize];

            setSize(this.gridSize * this.cellSize, this.gridSize * this.cellSize);
            setLayout(new GridLayout(this.gridSize, this.gridSize));

            for (int y = 0; y < this.gridSize; y++) {
                for (int x = 0; x < this.gridSize; x++) {
                    cells[y][x] = new Cell(y, x);
                    cells[y][x].setFocusPainted(false);
                    add(cells[y][x]);
                }
            }

            populateBombs();

            // must be done after bomb population 
            // as bombs have a separate action listener
            for (int y = 0; y < this.gridSize; y++) {
                for (int x = 0; x < this.gridSize; x++) {
                    final Cell cell = cells[y][x];
                    final Game self = this;

                    if (!cell.isBomb) {
                        cell.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                cell.reveal();
                                self.computeNeighboringBombs(cell);
                            }
                        });
                    }
                }
            }
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
