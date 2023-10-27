import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.Timer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.util.*;

class GameWonException extends Exception {
    GameWonException() {
        super("Game won");
    }
}

public class Game extends JFrame{
    public boolean firstCellRevealed;
    public boolean gameOver;
    public boolean hintMode;
    public boolean autoSolve;
    public int gridSize;
    public int cellSize;
    public int bombAmount;
    public Cell[][] cells;

    public int revealedCount;
    public Solver solver;

    public Timer timer;
    public JLabel mainLabel;
    public long time;
    public int remainingBombsCount;

    private ImageIcon gameIcon;

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

    public void updateHintMode() {
        this.hintMode = !this.hintMode;

        if (!this.firstCellRevealed) {
            return;
        }

        // if disabling hint mode, reset color of all cells unrevealed
        // also do this if gameover
        if(!this.hintMode || this.gameOver) {
            for(int y = 0; y < this.gridSize; y++) {
                for(int x = 0; x < this.gridSize; x++) {

                    if (this.cells[y][x].isRevealed) {
                        continue;
                    }

                    this.cells[y][x].setBackground(new Color(180, 180, 180));
                }
            }

            return;
        }

        // otherwise solve current situation to display hints
        this.solveSituation();
    }

    public void toggleAutoMode() {
       this.autoSolve = !this.autoSolve;
       System.out.println("Auto mode " + (this.autoSolve ? "ON" : "OFF"));

       if(!this.autoSolve || !this.hintMode) {
           return;
       }

       this.solveSituation();

       return;
    }

    public void revealBombs() {
        for(int y = 0; y < this.gridSize; y++) {
            for(int x = 0; x < this.gridSize; x++) {
                Cell currentCell = this.cells[y][x];

                if(currentCell.isBomb && !currentCell.isFlagged) {
                    currentCell.reveal();
                    continue;
                }
                if(!currentCell.isBomb && currentCell.isFlagged) {
                    currentCell.setBackground(Color.MAGENTA);
                }
            }
        }
    }

    // population of bombs is done based on a probability 
    // distribution where the likeliness of a cell being a 
    // bomb increases the further away it is from the first
    // clicked cell. 
    // this makes it more unlikely for the first clicked cell 
    // to be surrounded by bombs and allows for a more fluent 
    // user experience since it makes it less likely to have 
    // to guess when beginning the game, which greatly influences
    // whether or not you're going to have to guess later in the game.
    public void populateBombs(Cell exceptionCell, int remainingBombs) {
        Random random = new Random();

        // the loop increments in rings surrounding the 
        // exception cell, because if we simply loop linearly
        // through all cells than most of the bombs will be 
        // located around the corners, whereas this way we 
        // dont get rid of most of the available bombs right 
        // away at the cost of an extra indentation.
        ringloop:
        for (int d = 0; d < this.gridSize / 2; d++) {
            for (int y = exceptionCell.row - d; y < exceptionCell.row + d; y++) {
                
                if (y < 0 || y >= this.gridSize) {
                    continue;
                }

                for (int x = exceptionCell.col - d; x < exceptionCell.col + d; x++) {
                    if (x < 0 || x >= this.gridSize) {
                        continue;
                    }

                    Cell currentCell = this.cells[y][x];

                    double randomDouble = random.nextDouble(0, 1);
                    double probability = currentCell.calculateProbabilityOfBomb(exceptionCell.col, exceptionCell.row, this.gridSize);

                    if (randomDouble < probability) {
                        currentCell.makeBomb();
                        remainingBombs--;
                    }
                }

                if (remainingBombs <= 0) {
                    break ringloop;
                }
            }
        }

        if (remainingBombs > 0) {
            this.populateBombs(exceptionCell, remainingBombs);
        }
    }

    public void paintProbability(Cell exceptionCell) {
        for (int y = 0; y < this.gridSize; y++) {
            for (int x = 0; x < this.gridSize; x++) {
                Cell currentCell = this.cells[y][x];

                double probability = currentCell.calculateProbabilityOfBomb(exceptionCell.col, exceptionCell.row, this.gridSize);
                double intensity = Math.min(Math.log(1 / probability) * 20, 255);

                // the brighter the color, the more likely it is to
                // not have been selected as a bomb
                currentCell.setBackground(new Color(0, 0, (int)(intensity)));
            }
        }
    }

    public void computeNeighboringBombs(Cell cell) throws GameWonException {

        cell.reveal();
        this.revealedCount++;
        this.solver.reveal(cell);

        int neighboringBombs = 0;

        for(int y = cell.row - 1; y <= cell.row + 1; y++) {
            for(int x = cell.col - 1; x <= cell.col + 1; x++) {
                // skip out of bounds
                if(y < 0 || x < 0 || y >= this.gridSize || x >= this.gridSize) {
                    continue;
                }

                Cell currentCell = this.cells[y][x];

                // skip if cell is revealed
                if(currentCell.isRevealed) {
                    continue;
                }

                if (currentCell.isBomb) {
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

                // skip if cell is revealed, flagged, or a bomb
                if (currentCell.isRevealed || currentCell.isFlagged || currentCell.isBomb) {
                    continue;
                }

                computeNeighboringBombs(currentCell);
            }
        }

        // throw GameWonException at the end so that
        // the neighboringBombs count is set also for the 
        // final cells
        if (this.gridSize * this.gridSize - this.revealedCount == this.bombAmount) {
            throw new GameWonException();
        }
    }

    void solveSituation() {
        try {
            ArrayList<ArrayList<Cell>> solvedSituation = this.solver.solveSituation();

            for (Cell safe : solvedSituation.get(0)) {
                if (!this.cells[safe.row][safe.col].isRevealed) {
                    safe.markSafe();

                    if (!this.autoSolve) {
                        continue;
                    }

                    try {
                        this.computeNeighboringBombs(this.cells[safe.row][safe.col]);
                    }
                    catch (GameWonException e) {
                        this.handleGameWon();
                        System.out.println(e.getMessage());

                        return;
                    }
                }
            }
     
            if (this.autoSolve){
                this.solveSituation();
            }

            if (this.gameOver) {
                return;
            }

            for (Cell bomb : solvedSituation.get(1)) {
                bomb.markBomb();
            }

            for (Cell unknown : solvedSituation.get(2)) {
                unknown.markUnknown();
            }
        }
        catch (GuessRequiredException e) {
            for(Cell unknown : e.unknownCells) {
                unknown.markUnknown();
            }

            System.out.println(e.getMessage());

            return;
        }
    }

    void handleGameWon() {
        this.gameOver = true;
        this.updateHintMode();
        this.timer.stop();

        this.mainLabel.setText("B)");
    }

    public Game(int gridSize, int bombAmount){
        this.firstCellRevealed = false;
        this.gameOver = false;
        this.hintMode = false;
        this.autoSolve = false;
        this.cellSize = 35;
        this.gridSize = gridSize;
        this.bombAmount = bombAmount;
        this.cells = new Cell[this.gridSize][this.gridSize];
       
        this.revealedCount = 0;

        this.solver = new Solver(this);

        gameIcon = new ImageIcon(getClass().getResource("/res/logo.png"));
        setIconImage(gameIcon.getImage());

        this.remainingBombsCount = this.bombAmount;

        Game self = this; // utility

        setMinimumSize(new Dimension(650, 650));
        setSize(gridSize * cellSize, gridSize * cellSize);

        JPanel mineFieldPanel = new JPanel();
        JPanel gameStatsPanel = new JPanel();

        // Create components for game stats panel
        JPanel leftAlignPanel = new JPanel(new GridLayout());
        leftAlignPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        JPanel centerAlignPanel = new JPanel(new FlowLayout());
        JPanel rightAlignPanel = new JPanel(new FlowLayout());

        JLabel timeLabel = new JLabel(this.time + "s");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel remainingLabel = new JLabel(this.remainingBombsCount + " left");
        remainingLabel.setFont(new Font("Arial", Font.BOLD, 18));

        this.mainLabel = new JLabel(":)");
        this.mainLabel.setFont(new Font("Arial", Font.BOLD, 25));

        JButton menuButton = new JButton("Menu");
        menuButton.setFocusPainted(false);

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.stop();
            }
        }); 

        JButton hintButton = new JButton("Hint");
        hintButton.setFocusPainted(false);
        
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.updateHintMode();
            }
        }); 

        JButton autoButton = new JButton("Auto");
        autoButton.setFocusPainted(false);
        
        autoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.toggleAutoMode();
            }
        }); 

        leftAlignPanel.add(timeLabel);
        leftAlignPanel.add(remainingLabel);

        centerAlignPanel.add(this.mainLabel);

        rightAlignPanel.add(menuButton);
        rightAlignPanel.add(hintButton);
        rightAlignPanel.add(autoButton);

        gameStatsPanel.setLayout(new GridLayout());
        gameStatsPanel.add(leftAlignPanel);
        gameStatsPanel.add(centerAlignPanel);
        gameStatsPanel.add(rightAlignPanel);

        mineFieldPanel.setLayout(new GridLayout(gridSize, gridSize));

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

                            if(!currentCell.isRevealed){
                                self.remainingBombsCount += currentCell.isFlagged ? -1 : 1;
                                remainingLabel.setText(self.remainingBombsCount + " left");
                            }
                        }
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            if(currentCell.isFlagged) {
                                return;
                            }
                            else if(currentCell.isBomb) {
                                self.gameOver = true;
                                self.updateHintMode();
                                self.timer.stop();

                                try {    
                                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass()
                                    .getResource("/res/explosion.wav"));
                                    
                                    Clip clip = AudioSystem.getClip();
                                    clip.open(audioInputStream);
                                    clip.start();

                                } catch (Exception e) {
                                    System.out.println("Could not play audio file");
                                }

                                self.mainLabel.setText("x(");
                                self.revealBombs();

                                ImageIcon explosionIcon = new ImageIcon(getClass().getResource("/res/explosion.png"));
                                currentCell.setIcon(new ImageIcon(explosionIcon.getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH)));

                                System.out.println("Game lost");
                                return;
                            }
                            else if(currentCell.isRevealed) {
                                return;
                            }

                            if(!firstCellRevealed) {
                                self.populateBombs(currentCell, self.bombAmount);
                                self.timer.start();
                                firstCellRevealed = true;

                                self.paintProbability(currentCell);;
                            }

                            try {
                                self.computeNeighboringBombs(currentCell);
                            }
                            catch (GameWonException e) {
                                self.handleGameWon();
                                System.out.println(e.getMessage());

                                return;
                            }

                            if(self.hintMode) {
                                self.solveSituation();
                            }
                        }
                    }
                    public void mousePressed(MouseEvent me) {
                        if(SwingUtilities.isLeftMouseButton(me) && !self.gameOver) {
                            if(!currentCell.isRevealed) {
                                self.mainLabel.setText(":o");
                            }
                        }
                    }
                    public void mouseReleased(MouseEvent me) {
                        if(SwingUtilities.isLeftMouseButton(me) && !self.gameOver) {
                            if(!currentCell.isRevealed) {
                                self.mainLabel.setText(":)");
                            }
                        }
                    }
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
                timeLabel.setText(String.valueOf(time) + "s");
            }
        });
    }
}
