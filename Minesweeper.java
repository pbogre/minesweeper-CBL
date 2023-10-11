import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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
    private class Grid {
        private int gridSize;
        private Cell[][] cells;

        public Grid(int gridSize){
        
            this.gridSize = gridSize;
            this.cells = new Cell[gridSize][gridSize];

            frame = new JFrame("Minesweeper");
            frame.setSize(SIZE, SIZE);
            frame.setLayout(new BorderLayout());

            Container container = new Container();
            container.setLayout(new GridLayout(gridSize, gridSize));

            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    cells[row][col] = new Cell(row, col, actionListener);
                    container.add(cells[row][col]);
                }
            }
            
            frame.add(grid, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
    }
    public static void main(String[] args) {

        final int gridSize = 15; 
        Minesweeper minesweeper = new Minesweeper();
        Minesweeper.Grid grid = minesweeper.new Grid(gridSize);
    }
}
