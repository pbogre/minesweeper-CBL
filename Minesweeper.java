import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Minesweeper {

    // The size in pixels for the frame.
    private static final int WINDOW_SIZE = 500;
    private JFrame  frame;

    private final ActionListener actionListener = actionEvent -> {
        Object source = actionEvent.getSource();
    };

    private class Cell extends JButton {
        private final int row;
        private final int col;
        private int value;

        Cell(final int row, final int col,
             final ActionListener actionListener) {
            this.row = row;
            this.col = col;
            addActionListener(actionListener);
            setText("");
        }

        //TODO: For later implementations
        /*int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isAMine() {
            return value == MINE;
        }*/

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
  
    private class Grid extends JFrame{
        private int gridSize;
        private Cell[][] cells;

        public Grid(int gridSize){
        
            this.gridSize = gridSize;
            this.cells = new Cell[gridSize][gridSize];

            //new JFrame("Minesweeper");
            setSize(WINDOW_SIZE, WINDOW_SIZE);
            //frame.setLayout(new BorderLayout());

            //Container container = new Container();
            setLayout(new GridLayout(gridSize, gridSize));

            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    cells[row][col] = new Cell(row, col, actionListener);
                    add(cells[row][col]);
                }
            }
            
            //frame.add(container, BorderLayout.CENTER);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }
    }
    private class Menu extends JFrame {

        JButton startGame = new JButton("Start game", null);
        JButton selectDifficulty = new JButton("Select difficulty", null);

        private Menu()
        {
            setTitle("Game Frame");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 500); // Set the frame size
            setLocationRelativeTo(null); // Center the frame on the screen

            setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);

            // Add a big label for the title
            JLabel titleLabel = new JLabel("Minesweeper");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 40)); // Set a larger font

            constraints.gridx = 0;
            constraints.gridy = 0;
            add(titleLabel, constraints);

            JButton startGameButton = new JButton("Start Game");
            JButton selectDifficultyButton = new JButton("Select Difficulty");

            constraints.gridx = 0;
            constraints.gridy = 1;
            add(startGameButton, constraints);

            constraints.gridy = 2;
            add(selectDifficultyButton, constraints);

            setVisible(true);

            JFrame frame = this;
            startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                        //Calls grid and makes the grid/game window
                        Minesweeper.Grid grid = minesweeper.new Grid(15);
                        //Closes menu screen
                        frame.setVisible(false);
                        frame.dispose();
                }
            });
            
            //TODO: Select difficulty button functionality
        }
    }
    public static Minesweeper minesweeper;
    public static void main(String[] args) {

        minesweeper = new Minesweeper();
        Minesweeper.Menu menu = minesweeper.new Menu();
        /*final int gridSize = 15; 
        Minesweeper minesweeper = new Minesweeper();
        Minesweeper.Grid grid = minesweeper.new Grid(gridSize);*/
    }
}
