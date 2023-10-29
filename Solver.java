import java.util.*;


class GuessRequiredException extends Exception {

    ArrayList<Cell> unknownCells;
    /**
    * This exception is thrown when a guess is required to continue solving the game.
    */
    
    GuessRequiredException(ArrayList<Cell> unknownCells) {
        super("Guess required! Total Unknown: " + unknownCells.size());
        this.unknownCells = unknownCells;
    }
}
/**
 * The `Solver` class is used to solve a Minesweeper game. 
 * It contains methods for computing the number
 */

public class Solver {
    public int unrevealedSafe;
    public ArrayList<Cell> foundBombs;
    public ArrayList<Cell> foundSafe;
    public ArrayList<Cell> foundUnknown;
    private Game game;

    /**
     * The function removes a cell from the list of found unknown cells and decreases the count of
     * unrevealed safe cells if the cell is also found to be safe.
     * 
     * @param cell The parameter "cell" represents a specific cell in a game or grid.
     */
    public void reveal(Cell cell) {
        if (this.foundSafe.contains(cell)) {
            this.unrevealedSafe--;
        }
        this.foundUnknown.remove(cell);
    }

    // basically: if the amount of neighboringBombs of any cells 
    // is equal to the number of cells that are 100% bombs neighboring it,
    // then all other neighboring unrevealed cells must be safe
    //
    // not so basically: 
    // for each revealed cell where neighboringBombs != 0
    //   neighboringBombCount = 0
    //   for each unrevealed neighboring cell
    //     if this neighboring cell is 100% a bomb 
    //      neighboringBombCount++
    //   if neighboringBombCount == this cell's neighboringBombs
    //     all unrevealed neighboring cells that aren't 100% bombs must be safe

    /**
     * The function `computeSafe()` iterates through 
     * the cells of a game grid and determines if a cell
     * is safe based on the number of neighboring bombs 
     * and the cells that have already been revealed.
     * 
     * @return The method `computeSafe()` returns an integer value, which represents the number of
     *      newly found safe cells.
     */
    private int computeSafe() {
        int newlyFoundSafe = 0;

        for (int y = 0; y < this.game.gridSize; y++) {
            for (int x = 0; x < this.game.gridSize; x++) {
                Cell cell = this.game.cells[y][x];

                if (cell.neighboringBombs == 0 || !cell.isRevealed) {
                    continue;
                }

                int neighboringFoundBombs = 0;
                ArrayList<Cell> neighboringPossible = new ArrayList<Cell>();
                for (int ny = y - 1; ny <= y + 1; ny++) {
                    for (int nx = x - 1; nx <= x + 1; nx++) {

                        if (ny < 0 || nx < 0 || ny >= this.game.gridSize 
                            || nx >= this.game.gridSize) {
                            continue;
                        }

                        if (ny == y && nx == x) {
                            continue;
                        }

                        Cell neighboringCell = this.game.cells[ny][nx];

                        if (neighboringCell.isRevealed) {
                            continue;
                        }

                        if (this.foundBombs.contains(neighboringCell)) {
                            neighboringFoundBombs++;
                        } else if (!this.foundSafe.contains(neighboringCell)) {
                            neighboringPossible.add(neighboringCell);
                        }
                    }
                }

                if (neighboringFoundBombs == cell.neighboringBombs) {
                    for (Cell safe : neighboringPossible) {
                        newlyFoundSafe++;
                        unrevealedSafe++;
                        this.foundSafe.add(safe);

                        this.foundUnknown.remove(safe);
                    }
                } else {
                    for (Cell unknown : neighboringPossible) {
                        if (!this.foundBombs.contains(unknown) 
                            && !this.foundUnknown.contains(unknown)) {
                            this.foundUnknown.add(unknown);
                        }
                    }
                }
            }
        }

        return newlyFoundSafe;
    }

    // basically: if the amount of unrevealed neighboring cells of
    // any cell where there exist some neighboring bombs is equal 
    // to the amount of possible neighboring bombs (i.e. neighboring 
    // unrevealed cells that haven't been marked as 100% safe),
    // then all of those possible neighboring bombs must be bombs
    //
    // not so basically: 
    // for each revealed cell where neighboringBombs != 0
    //   possibleBombCount = 0
    //   for each unrevealed cell neighboring this cell that is not 100% safe 
    //     possibleBombCount++
    //   if possibleBombCount == this cell's neighboringBombs 
    //     all unrevealed neighboring cells that aren't 100% safe are bombs

    /**
     * The function computes the number of newly 
     * found bombs based on the neighboring cells of revealed
     * cells in a game grid.
     * 
     * @return The method `computeBombs()` returns an integer value, which represents the number of
     *      newly found bombs.
     */
    private int computeBombs() {
        int newlyFoundBombCount = 0;

        for (int y = 0; y < this.game.gridSize; y++) {
            for (int x = 0; x < this.game.gridSize; x++) {
                Cell cell = this.game.cells[y][x];

                if (cell.neighboringBombs == 0 || !cell.isRevealed) {
                    continue;
                }

                ArrayList<Cell> neighboringPossible = new ArrayList<Cell>();
                for (int ny = y - 1; ny <= y + 1; ny++) {
                    for (int nx = x - 1; nx <= x + 1; nx++) {

                        if (ny < 0 || nx < 0 || ny >= this.game.gridSize 
                            || nx >= this.game.gridSize) {
                            continue;
                        }

                        if (ny == y && nx == x) {
                            continue;
                        }

                        Cell neighboringCell = this.game.cells[ny][nx];

                        if (neighboringCell.isRevealed) {
                            continue;
                        }

                        if (!this.foundSafe.contains(neighboringCell)) {
                            neighboringPossible.add(neighboringCell);
                        }
                    }
                }

                if (neighboringPossible.size() == cell.neighboringBombs) {
                    for (Cell bomb : neighboringPossible) {
                        if (!this.foundBombs.contains(bomb)) {
                            newlyFoundBombCount++;
                            this.foundBombs.add(bomb);

                            this.foundUnknown.remove(bomb);
                        }
                    }
                }
            }
        }

        return newlyFoundBombCount;
    }

    // returns list of 2 lists;
    // first list: 100% safe cells 
    // second list: 100% bomb cells 
    //
    // we iterate the two solver steps until they produce no new results
    // this is done because often more bombs can be found from the newly 
    // found safe cells even if no new cells were revealed and vice versa
    //
    // bombs are computed before safe cells because at least 
    // some 100% bombs are required to find 100% safe cells

    /**
     * The function "solveSituation" attempts to 
     * solve a situation by iteratively computing the number
     * of bombs and safe cells until no new bombs or safe 
     * cells are found, and then returns the solved
     * situation as an ArrayList of ArrayLists containing 
     * the found safe cells, found bombs, and found
     * unknown cells.
     * 
     * @return The method is returning an ArrayList of ArrayLists of Cell objects.
     */
    public ArrayList<ArrayList<Cell>> solveSituation() throws GuessRequiredException {

        int newlyFoundBombs = computeBombs();
        int newlyFoundSafe = computeSafe();

        while (newlyFoundBombs != 0 && newlyFoundSafe != 0) {
            newlyFoundBombs = this.computeBombs();
            newlyFoundSafe = this.computeSafe();
        }

        // if solved situation has no newly found safe cells 
        // and there are no more safe cells to be revealed
        if (newlyFoundSafe == 0 && newlyFoundBombs == 0 && this.unrevealedSafe <= 0) {
            throw new GuessRequiredException(this.foundUnknown);
        }

        ArrayList<ArrayList<Cell>> solvedSituation = new ArrayList<ArrayList<Cell>>();
        solvedSituation.add(this.foundSafe);
        solvedSituation.add(this.foundBombs);
        solvedSituation.add(this.foundUnknown);

        return solvedSituation;
    }

    /**
     * The `Solver` constructor initializes a new instance of the `Solver` class with the specified
     * game.
     * 
     * @param game The parameter "game" represents a Minesweeper game.
     */
    Solver(Game game) {
        this.game = game;
        this.unrevealedSafe = 0;

        this.foundBombs = new ArrayList<Cell>();
        this.foundSafe = new ArrayList<Cell>();
        this.foundUnknown = new ArrayList<>();
    }
}
