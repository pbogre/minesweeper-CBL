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
    public Cell firstCell;

    public boolean gameOver;
    public boolean hintMode;
    public boolean autoSolve;

    public int maxProbability;
    public boolean drawProbabilities;
    public boolean drawPopulationRings;

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



    /**
     * The stop() function hides the current window, disposes of it, and then creates and runs a new
     * Menu window.
     */
    public void stop() {
        setVisible(false);
        dispose();

        Menu menu = new Menu(500);
        menu.run();
    }

    /**
     * The function sets the location of the JFrame to the center of the screen, sets the default close
     * operation to exit the program when the frame is closed, and makes the frame visible.
     */
    public void run() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * The function `updateHintMode` toggles the hint mode and updates the display of hints based on
     * the current game situation.
     */
    public void updateHintMode() {
        this.hintMode = !this.hintMode;

        if (this.firstCell == null) {
            return;
        }

        // if disabling hint mode, reset color of all cells unrevealed
        // also do this if gameover
        if(!this.hintMode || this.gameOver) {
            this.resetCells();

            return;
        }

        // otherwise solve current situation to display hints
        this.solveSituation();
    }

    /**
     * The function reveals all the bombs on the grid and highlights any incorrectly flagged cells.
     */
    public void toggleAutoMode() {
        
        this.autoSolve = !this.autoSolve;
        System.out.println("Auto mode " + (this.autoSolve ? "ON" : "OFF"));

        if (!this.autoSolve || !this.hintMode) {
           return;
        }

        this.solveSituation();
    }

    /**
     * The function resets the cells in a grid by changing their background color and removing any
     * text.
     */
    public void resetCells() {

        if (this.drawProbabilities) {
            this.paintProbabilities();

            return;
        }

        if (this.drawPopulationRings) {
            this.paintPopulationRings();

            return;
        }

        for(int y = 0; y < this.gridSize; y++) {
            for(int x = 0; x < this.gridSize; x++) {
                Cell currentCell = this.cells[y][x];

                if (currentCell.isRevealed) {
                    continue;
                }

                currentCell.setBackground(new Color(180, 180, 180));
                currentCell.setText("");
            }
        }
    }

    /**
     * The function reveals all the bombs on the grid and highlights any incorrectly flagged cells.
     */
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

    /**
     * The function populates the grid with bombs based on their probability of being a bomb, which is a
     * distribution where the chance of a cell being a bomb increases the further away it is from the 
     * first clicked cell. This makes it more unlikely for the first clicked cell to be surrounded by 
     * bombs and allows for a more fluent user experience since it makes it less likely to have to guess.
     * 
     * @param remainingBombs The remainingBombs parameter represents the number of bombs that still
     * need to be placed on the grid.
     */
    public void populateBombsProbability(int remainingBombs) {
        Random random = new Random();

        // the loop increments in rings surrounding the 
        // exception cell, because if we simply loop linearly
        // through all cells then most of the bombs will be 
        // located around the corners, whereas this way we 
        // dont get rid of most of the available bombs right 
        // away
        int largestRing = 1 + this.gridSize - Math.min(this.firstCell.row, this.firstCell.col);
        ringloop:
        for (int d = 1; d <= largestRing; d++) {
            for (int y = this.firstCell.row - d; y <= this.firstCell.row + d; y++) {
 
                // skip if out of bounds
                if (y < 0 || y >= this.gridSize) {
                    continue;
                }

                for (int x = this.firstCell.col - d; x <= this.firstCell.col + d; x++) {

                    // skip if inside ring (dont want to iterate over previous ring)
                    if (x > this.firstCell.col - d  && x < this.firstCell.col + d &&
                        y > this.firstCell.row - d  && y < this.firstCell.row + d ) {
                        continue;
                    }

                    // skip if out of bounds
                    if (x < 0 || x >= this.gridSize) {
                        continue;
                    }

                    Cell currentCell = this.cells[y][x];

                    // skip if already bomb
                    if (currentCell.isBomb) {
                        continue;
                    }

                    double randomDouble = random.nextDouble(0, 1);
                    double probability = currentCell.calculateProbabilityOfBomb(this.firstCell.col, this.firstCell.row, this.gridSize, this.maxProbability);

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

        // if we haven't finished populating, restart
        if (remainingBombs > 0) {
            this.populateBombsProbability(remainingBombs);
        }
    }

    /**
     * The function populates a grid with a random number of bombs, excluding the first revealed cell.
     */
    public void populateBombsRandom() {
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
            if (randomCell.row == this.firstCell.row && randomCell.col == this.firstCell.col) {
                continue;
            }

            randomCell.makeBomb();
            remainingBombs--;
        }
    }

    /**
     * The function paints the probability of a cell being a bomb on the grid with the implemented distribution
     */
    public void paintProbabilities() {
        for (int y = 0; y < this.gridSize; y++) {
            for (int x = 0; x < this.gridSize; x++) {
                Cell currentCell = this.cells[y][x];

                if (currentCell.isRevealed) {
                    continue;
                }

                double probability = currentCell.calculateProbabilityOfBomb(this.firstCell.col, this.firstCell.row, this.gridSize, this.maxProbability);
                double intensity = probability * (100 / this.maxProbability) * 255;

                currentCell.setBackground(new Color(0, 0, (int)(intensity)));
                currentCell.setForeground(Color.YELLOW);
                currentCell.setText((int)(probability * 100) + "%");
            }
        }
    }

    // this method serves as a demonstation
    // that the way the iteration is done 
    // in the probability population method 
    // works as intended
    /**
     * The function "paintPopulationRings" iterates over cells in a grid and sets their background
     * color, foreground color, and text based on their distance from a specified cell in concetric rings
     * to ensure bombs aren't focused in the corners of the grid.
     */
    public void paintPopulationRings() {
        int largestRing = 1 + this.gridSize - Math.min(this.firstCell.row, this.firstCell.col);

        for (int d = 1; d <= largestRing; d++) {
            for (int y = this.firstCell.row - d; y <= this.firstCell.row + d; y++) {
 
                // skip if out of bounds
                if (y < 0 || y >= this.gridSize) {
                    continue;
                }

                for (int x = this.firstCell.col - d; x <= this.firstCell.col + d; x++) {

                    // skip if inside ring (dont want to iterate over previous ring)
                    if (x > this.firstCell.col - d  && x < this.firstCell.col + d &&
                        y > this.firstCell.row - d  && y < this.firstCell.row + d ) {
                        continue;
                    }

                    // skip if out of bounds
                    if (x < 0 || x >= this.gridSize) {
                        continue;
                    }

                    Cell currentCell = this.cells[y][x];

                    // skip if revealed
                    if (currentCell.isRevealed) {
                        continue;
                    }

                    currentCell.setBackground(d % 2 == 0 ? Color.WHITE : Color.BLACK);
                    currentCell.setForeground(d % 2 == 0 ? Color.BLACK : Color.WHITE);
                    currentCell.setText(String.valueOf(d));
                }
            }
        }
    }

    /**
     * The function computes the number of neighboring bombs for a given cell in a Minesweeper game and
     * recursively reveals its neighbors if there are no adjacent bombs.
     * 
     * @param cell The parameter "cell" represents a specific cell in the grid. It is an instance of the
     * "Cell" class, which has properties such as "row" and "col" to represent its position in
     * the grid, as well as boolean properties like "isRevealed", "isFlag" and "isBomb".
     */
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

    /**
     * The function attempts to solve a Minesweeper situation by marking cells as safe, bombs, or
     * unknown based on the solver's output.
     */
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

    // The above code is defining a constructor for a Game class in Java. The constructor takes in two
    // parameters: gridSize and bombAmount.
    void handleGameWon() {
        this.gameOver = true;
        this.updateHintMode();
        this.timer.stop();

        this.mainLabel.setText("B)");
    }

    public Game(int gridSize, int bombAmount, int maxProbability, 
                boolean useProbability, boolean drawProbabilities, boolean drawPopulationRings){
        this.firstCell = null;

        this.gameOver = false;
        this.hintMode = false;
        this.autoSolve = false;

        this.drawProbabilities = drawProbabilities;
        this.drawPopulationRings = drawPopulationRings;
        this.maxProbability = maxProbability;

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

                            if(firstCell == null) {
                                self.firstCell = currentCell;

                                if (useProbability) {
                                    self.populateBombsProbability(self.bombAmount);
                                } else {
                                    self.populateBombsRandom();
                                }

                                self.timer.start();

                                if (self.drawProbabilities) {
                                    self.paintProbabilities();
                                }
                                if (self.drawPopulationRings) {
                                    self.paintPopulationRings();
                                }
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
