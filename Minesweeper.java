import javax.swing.*;

class Cell {
    // game related stuff
    boolean bomb;
    boolean revealed;
    int x;
    int y;

    // swing related stuff
    JButton button;

    void render(JFrame frame){
        frame.add(this.button);
    }

    void makeBomb() {
        this.bomb = true;
    }

    Cell(int x, int y){
        this.x = x;
        this.y = y;
        this.bomb = false;
        this.revealed = false;
        this.button = new JButton();
    }
}

class Grid {
    int size;
    Cell[][] grid;

    int cellSize;

    void render(JFrame frame){
        for(int y = 0; y < this.size; y++) {
            for(int x = 0; x < this.size; x++) {
                this.grid[y][x].render(frame);
            }
        }
    }

    Grid(int size, int cellSize) {
        this.size = size;
        this.cellSize = cellSize;
        this.grid = new Cell[size][size];

        for(int y = 0; y < this.size; y++) {
           for(int x = 0; x < this.size; x++) {
               this.grid[y][x] = new Cell(x, y);
               this.grid[y][x].button.setBounds(this.cellSize * x, this.cellSize * y, this.cellSize, this.cellSize);
           }
        }
    }
}

class Minesweeper {
    static int WIDTH = 500;
    static int HEIGHT = 500;
    static int SIZE = 20;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Minesweeper");

        int cellSize = HEIGHT / SIZE;
        Grid grid = new Grid(SIZE, cellSize);
        grid.render(frame);

        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
