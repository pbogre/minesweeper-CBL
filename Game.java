import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.Timer;

public class Game extends JFrame{
    public boolean firstCellRevealed;
    public boolean gameOver;
    public int gridSize;
    public int cellSize;
    public int bombAmount;
    public Cell[][] cells;

    public Timer timer;
    public long time;
    public int remainingBombsCount;

    public void stop() {
        setVisible(false);
        dispose();

        Menu menu = new Menu(500);
        menu.run();
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
        this.firstCellRevealed = false;
        this.gameOver = false;
        this.cellSize = 35;
        this.gridSize = gridSize;
        this.bombAmount = bombAmount;
        this.cells = new Cell[this.gridSize][this.gridSize];

        this.remainingBombsCount = this.bombAmount;

        Game self = this; // utility

        setMinimumSize(new Dimension(500, 500));
        setSize(gridSize * cellSize, gridSize * cellSize);

        JPanel mineFieldPanel = new JPanel();
        JPanel gameStatsPanel = new JPanel();

        // Create components for game stats panel
        JLabel timeLabel = new JLabel("Time: " + this.time);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel mainLabel = new JLabel("Minesweeper");
        mainLabel.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel remainingLabel = new JLabel("Remaining: " + this.remainingBombsCount);
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.stop();
            }
        }); 

        // TODO: left/right/center alignment
        gameStatsPanel.setLayout(new FlowLayout());
        gameStatsPanel.add(timeLabel);
        gameStatsPanel.add(mainLabel);
        gameStatsPanel.add(remainingLabel);
        gameStatsPanel.add(menuButton);

        mineFieldPanel.setLayout(new GridLayout(gridSize, gridSize));

        /*setLayout(new GridBagLayout());
        setSize(this.gridSize * this.cellSize, (this.gridSize * this.cellSize) + 150);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        mineFieldPanel.setSize(this.gridSize * this.cellSize, this.gridSize * this.cellSize);
        mineFieldPanel.setLayout(new GridLayout(this.gridSize, this.gridSize));

        gameStatsPanel.setSize(this.gridSize * this.cellSize, 150);*/

        for (int y = 0; y < this.gridSize; y++) {
            for (int x = 0; x < this.gridSize; x++) {
                cells[y][x] = new Cell(y, x);
                Cell currentCell = this.cells[y][x];
                currentCell.setFocusPainted(false);

                currentCell.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent me) {
                        if(self.gameOver) {
                            return;
                        }

                        if (SwingUtilities.isRightMouseButton(me)) {
                            currentCell.toggleFlag();
                            if(!currentCell.isRevealed && self.remainingBombsCount != 0){
                                self.remainingBombsCount += currentCell.isFlagged ? -1 : 1;
                            }
                            else if(self.remainingBombsCount == 0){
                                currentCell.setIcon(null);
                                self.remainingBombsCount++;
                            }
                            remainingLabel.setText("Remaining: " + self.remainingBombsCount);
                        }
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            if(currentCell.isFlagged) {
                                return;
                            }
                            if(currentCell.isBomb) {
                                self.gameOver = true;
                                self.timer.stop();
                                self.revealBombs();
                                return;
                            }

                            if(!firstCellRevealed) {
                                self.populateBombs(currentCell);
                                firstCellRevealed = true;
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

                mineFieldPanel.add(currentCell);
            }
        }
        add(gameStatsPanel, BorderLayout.NORTH);
        add(mineFieldPanel);
        setVisible(true);

        time = 0;
        this.timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time++;
                timeLabel.setText("Time: " + String.valueOf(time));
            }
        });
        this.timer.start();
    }
}
