import java.util.*;

public class Solver {
    private ArrayList<Cell> foundBombs;
    private ArrayList<Cell> foundSafe;
    private Game game;

    private int computeSafe() {
        for(int y = 0; y < this.game.gridSize; y++) {
            for(int x = 0; x < this.game.gridSize; x++) {
                Cell cell = this.game.cells[y][x];

                if(!cell.isRevealed) {
                    continue;
                }

                int neighboringBombs = 0;
                int neighboringPossible = 0;
                for(int ny = y - 1; ny <= y + 1; ny++) {
                    for(int nx = x - 1; nx <= x +1; nx++ ) {

                        if(ny < 0 || nx < 0 || ny >= this.game.gridSize || nx >= this.game.gridSize) {
                            continue;
                        }

                        if(ny == y && nx == x) {
                            continue;
                        }

                        Cell neighboringCell = this.game.cells[ny][nx];

                        if(!neighboringCell.isRevealed && !this.foundSafe.contains(neighboringCell)) {
                            if(this.foundBombs.contains(neighboringCell)) {
                                neighboringBombs++;
                            }
                        }

                        neighboringPossible++;
                    }
                }

                if (neighboringBombs == neighboringPossible) {
                    this.foundSafe.add(cell);
                }
            }
        }

        return this.foundSafe.size();
    }

    // TODO also take into account remaining total bombs, eg total remaining cells = total remaining bombs?
    private int computeBombs() {
        for(int y = 0; y < this.game.gridSize; y++) {
            for(int x = 0; x < this.game.gridSize; x++) {
                Cell cell = this.game.cells[y][x];

                if(cell.neighboringBombs == 0 || !cell.isRevealed) {
                    continue;
                }

                cell.markSource();

                ArrayList<Cell> neighboringPossible = new ArrayList<Cell>();
                for(int ny = y - 1; ny <= y + 1; ny++) {
                    for(int nx = x - 1; nx <= x +1; nx++ ) {

                        if(ny < 0 || nx < 0 || ny >= this.game.gridSize || nx >= this.game.gridSize) {
                            continue;
                        }

                        if(ny == y && nx == x) {
                            continue;
                        }

                        Cell neighboringCell = this.game.cells[ny][nx];

                        if (neighboringCell.isRevealed) {
                            continue;
                        }

                        neighboringCell.markUnknown();

                        if(!this.foundSafe.contains(neighboringCell)) {
                            neighboringPossible.add(neighboringCell);
                        }
                    }
                }

                //System.out.println("("+cell.col+", "+cell.row+") - " + "cell.neighboringBombs: " + cell.neighboringBombs + ", neighboringPossible.size(): " + neighboringPossible.size());

                if(neighboringPossible.size() == cell.neighboringBombs) {
                    for(Cell bomb : neighboringPossible) {
                        if (!this.foundBombs.contains(bomb)) {
                            this.foundBombs.add(bomb);
                        }
                    }
                }
            }
        }

        return this.foundBombs.size();
    }

    // { { safe… }, { bomb… } }
    // when newly found safe bombs == 0: must guess situation
    public ArrayList<ArrayList<Cell>> solveSituation() {

        int currentFoundBombs = computeBombs();
        int currentFoundSafe = computeSafe();

        int iteration = 0;
        System.out.println("["+iteration+"] Bombs: " + currentFoundBombs + ", Safe: " + currentFoundSafe);
        while (this.foundBombs.size() != currentFoundBombs || this.foundSafe.size() != currentFoundSafe) {
            currentFoundBombs = this.computeBombs();
            currentFoundSafe = this.computeSafe();

            System.out.println("["+iteration+"] Bombs: " + currentFoundBombs + ", Safe: " + currentFoundSafe);
            iteration++;
        }

        ArrayList<ArrayList<Cell>> solvedSituation = new ArrayList<ArrayList<Cell>>();
        solvedSituation.add(this.foundSafe);
        solvedSituation.add(this.foundBombs);

        return solvedSituation;
    }

    Solver(Game game) {
        this.game = game;
        this.foundBombs = new ArrayList<Cell>();
        this.foundSafe = new ArrayList<Cell>();
    }
}
