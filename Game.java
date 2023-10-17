import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Game extends JFrame{
    public boolean gameOver;
    public int gridSize;
    public int cellSize;
    public int bombAmount;
    public Cell[][] cells;

    public void stop() {
        setVisible(false);
        dispose();
    }

    public void run() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void revealBombs() {
        for(int y = 0; y < this.gridSize; y++) {
            for(int x = 0; x < this.gridSize; x++) {
                if(this.cells[y][x].isBomb) {
                    this.cells[y][x].reveal();
                }
            }
        }
    }

    public void populateBombs() {
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

    public void computeNeighboringBombs(Cell cell) {

        if(cell.isBomb) {
            return;
        }

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
                computeNeighboringBombs(currentCell);
            }
        }
    }

    public Game(int gridSize, int bombAmount){
        this.gameOver = false;
        this.cellSize = 35;
        this.gridSize = gridSize;
        this.bombAmount = bombAmount;
        this.cells = new Cell[this.gridSize][this.gridSize];

        setSize(this.gridSize * this.cellSize, this.gridSize * this.cellSize);
        setLayout(new GridLayout(this.gridSize, this.gridSize));

        for (int y = 0; y < this.gridSize; y++) {
            for (int x = 0; x < this.gridSize; x++) {
                cells[y][x] = new Cell(y, x);
                Cell currentCell = this.cells[y][x];
                currentCell.setFocusPainted(false);

                Game self = this;
                currentCell.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent me) {
                        if(self.gameOver) {
                            return;
                        }

                        if (SwingUtilities.isRightMouseButton(me)) {
                            currentCell.flag();
                        }
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            if(currentCell.isBomb) {
                                self.gameOver = true;
                                self.revealBombs();
                                return;
                            }

                            currentCell.reveal();
                            self.computeNeighboringBombs(currentCell);
                        }
                    }
                    public void mousePressed(MouseEvent me) {}
                    public void mouseReleased(MouseEvent me) {}
                    public void mouseEntered(MouseEvent me) {}
                    public void mouseExited(MouseEvent me) {}
                });

                add(currentCell);
            }
        }

        populateBombs();
    }
}
