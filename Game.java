import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

class GameWonException extends Exception {
    GameWonException() {
        super("Game won");
    }
}

public class Game extends JFrame{
    public boolean firstCellRevealed;
    public boolean gameOver;
    public int gridSize;
    public int cellSize;
    public int bombAmount;
    public Cell[][] cells;

    public int revealedCount;

    public Solver solver;

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

    public void populateBombs(Cell exceptionCell) {
        Random random = new Random();
        int remainingBombs = this.bombAmount;

        while (remainingBombs > 0) {
            int randomColumn = random.nextInt(this.gridSize);
            int randomRow = random.nextInt(this.gridSize);
            Cell randomCell = this.cells[randomRow][randomColumn];
           
            // skip if already bomb
            if (randomCell.isBomb) {
                continue;
            }
            // skip if is exception cell (first cell revealed)
            if (randomCell.row == exceptionCell.row && randomCell.col == exceptionCell.col) {
                continue;
            }

            randomCell.makeBomb();
            remainingBombs--;
        }
    }

    public void computeNeighboringBombs(Cell cell) throws GameWonException {

        cell.reveal();
        this.revealedCount++;

        if (this.gridSize * this.gridSize - this.revealedCount == this.bombAmount) {
            throw new GameWonException();
        }

        this.solver.reveal(cell);

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

                computeNeighboringBombs(currentCell);
            }
        }
    }

    void solveSituation() {
        try {
            ArrayList<ArrayList<Cell>> solvedSituation = solver.solveSituation();

            for(Cell safe : solvedSituation.get(0)) {
                if(!this.cells[safe.row][safe.col].isRevealed) {
                    safe.markSafe();

                    // uncomment stuff below for recursive solver 
                    // i.e., automatically win games if possible
                    //try {
                    //    this.computeNeighboringBombs(this.cells[safe.row][safe.col]);
                    //}
                    //catch (GameWonException e) {
                    //    System.out.println(e.getMessage());
                    //    this.gameOver = true;
                    //}

                    //this.solveSituation();
                }
            }

            for(Cell bomb : solvedSituation.get(1)) {
                bomb.markBomb();
            }
        }
        catch (GuessRequiredException e) {
            System.out.println(e.getMessage());
            return;
        }
        // TODO handle the exception that happens on guess required sometimes
        catch (Exception e) {
            System.out.println("AN EXCEPTION OCCURED");
        }
    }

    public Game(int gridSize, int bombAmount){
        this.firstCellRevealed = false;
        this.gameOver = false;
        this.cellSize = 35;
        this.gridSize = gridSize;
        this.bombAmount = bombAmount;
        this.cells = new Cell[this.gridSize][this.gridSize];
       
        this.revealedCount = 0;

        this.solver = new Solver(this);

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
                            currentCell.toggleFlag();
                        }
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            if(currentCell.isFlagged) {
                                return;
                            }
                            if(currentCell.isBomb) {
                                self.gameOver = true;
                                System.out.println("Game lost");
                                self.revealBombs();
                                return;
                            }
                            if(currentCell.isRevealed) {
                                return;
                            }

                            if(!firstCellRevealed) {
                                self.populateBombs(currentCell);
                                firstCellRevealed = true;
                            }

                            try {
                                self.computeNeighboringBombs(currentCell);
                            }
                            catch (GameWonException e) {
                                System.out.println(e.getMessage());
                                self.gameOver = true;
                                return;
                            }

                            self.solveSituation();
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
    }
}
