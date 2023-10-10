import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.awt.event.*;

public class MinesweeperTest {
    
    // The value assigned to cells marked as mines. 
    // 10 works because no cell will have more than 8 neighbouring mines.
    private static final int MINE = 10;

    // The size in pixels for the frame.
    private static final int SIZE = 500;

    private int gridSize;

    private Cell[][] cells;

    private JFrame  frame;

    private final ActionListener actionListener = actionEvent -> {
        Object source = actionEvent.getSource();
    };

    private class Cell extends JButton {
        private final int row;
        private final int col;
        private       int value;

        Cell(final int row, final int col,
             final ActionListener actionListener) {
            this.row = row;
            this.col = col;
            addActionListener(actionListener);
            setText("");
        }

        int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isAMine() {
            return value == MINE;
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
    private MinesweeperTest(final int gridSize) {
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];

        frame = new JFrame("Minesweeper");
        frame.setSize(SIZE, SIZE);
        frame.setLayout(new BorderLayout());

        //initializeButtonPanel();
        initializeGrid();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initializeGrid() {
        Container grid = new Container();
        grid.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(row, col, actionListener);
                grid.add(cells[row][col]);
            }
        }
        //createMines();
        frame.add(grid, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        //Amount of buttons per row/col
        final int gridSize = 10;
        new MinesweeperTest(gridSize);
    }   
}
