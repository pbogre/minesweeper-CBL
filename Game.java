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
    public int gridSize;
    public int cellSize;
    public int bombAmount;
    public Cell[][] cells;

    public int revealedCount;
    public Solver solver;

    public Timer timer;
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

    public void toggleHintMode() {
        this.hintMode = !this.hintMode;

        if (!this.firstCellRevealed) {
            return;
        }

        // if disabling hint mode, reset color of all cells unrevealed
        if(!this.hintMode) {
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

            for(Cell unknown : solvedSituation.get(2)) {
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
        // TODO handle the exception that happens on guess required sometimes
        catch (Exception e) {
            System.out.println("AN EXCEPTION OCCURED");
        }
    }

    public Game(int gridSize, int bombAmount){
        this.firstCellRevealed = false;
        this.gameOver = false;
        this.hintMode = false;
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

        setMinimumSize(new Dimension(500, 500));
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

        JLabel mainLabel = new JLabel(":)");
        mainLabel.setFont(new Font("Arial", Font.BOLD, 25));

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
                self.toggleHintMode();
            }
        }); 

        leftAlignPanel.add(timeLabel);
        leftAlignPanel.add(remainingLabel);

        centerAlignPanel.add(mainLabel);

        rightAlignPanel.add(menuButton);
        rightAlignPanel.add(hintButton);

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

                                mainLabel.setText("x(");
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
                                self.populateBombs(currentCell);
                                self.timer.start();
                                firstCellRevealed = true;
                            }

                            try {
                                self.computeNeighboringBombs(currentCell);
                            }
                            catch (GameWonException e) {
                                self.gameOver = true;
                                self.timer.stop();

                                mainLabel.setText("B)");

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
                                mainLabel.setText(":o");
                            }
                        }
                    }
                    public void mouseReleased(MouseEvent me) {
                        if(SwingUtilities.isLeftMouseButton(me) && !self.gameOver) {
                            if(!currentCell.isRevealed) {
                                mainLabel.setText(":)");
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
