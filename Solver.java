import java.util.*;

class GuessRequiredException extends Exception {
    GuessRequiredException(int unknownCount, int unrevealedSafe) {
        super("[GUESS] Total Unknown: " + unknownCount + ", Unrevealed Safe: " + unrevealedSafe );
    }
}

// TODO this exception and its handling should be done in Game.java
class GameWonException extends Exception {
    GameWonException() {
        super("Game won");
    }
}

public class Solver {
    public int unrevealedSafe;
    public ArrayList<Cell> foundBombs;
    public ArrayList<Cell> foundSafe;
    public ArrayList<Cell> foundUnknown;
    private Game game;

    public void unreveal(Cell cell) {
        if(this.foundSafe.contains(cell)) {
            this.unrevealedSafe--;
        }
        this.foundUnknown.remove(cell);
    }

    private int computeSafe() {
        int newlyFoundSafe = 0;

        for(int y = 0; y < this.game.gridSize; y++) {
            for(int x = 0; x < this.game.gridSize; x++) {
                Cell cell = this.game.cells[y][x];

                if(cell.neighboringBombs == 0 || !cell.isRevealed) {
                    continue;
                }

                int neighboringFoundBombs = 0;
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

                        if(neighboringCell.isRevealed) {
                            continue;
                        }

                        if(this.foundBombs.contains(neighboringCell)) {
                            neighboringFoundBombs++;
                        } else if (!this.foundSafe.contains(neighboringCell)){
                            neighboringPossible.add(neighboringCell);
                        }
                    }
                }

                //System.out.println("[SAFE] ("+cell.col+", "+cell.row+") - " + "cell.neighboringBombs: " + cell.neighboringBombs + ", neighboringFoundBombs: " + neighboringFoundBombs);

                if (neighboringFoundBombs == cell.neighboringBombs) {
                    for(Cell safe : neighboringPossible) {
                        newlyFoundSafe++;
                        unrevealedSafe++;
                        this.foundSafe.add(safe);

                        this.foundUnknown.remove(safe);
                    }
                } else {
                    for(Cell unknown : neighboringPossible) {
                        if(!this.foundBombs.contains(unknown) && !this.foundUnknown.contains(unknown)) {
                            this.foundUnknown.add(unknown);
                            unknown.markUnknown();
                        }
                    }
                }
            }
        }

        return newlyFoundSafe;
    }

    private int computeBombs() {
        int newlyFoundBombCount = 0;

        for(int y = 0; y < this.game.gridSize; y++) {
            for(int x = 0; x < this.game.gridSize; x++) {
                Cell cell = this.game.cells[y][x];

                if(cell.neighboringBombs == 0 || !cell.isRevealed) {
                    continue;
                }

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

                        if(!this.foundSafe.contains(neighboringCell)) {
                            neighboringPossible.add(neighboringCell);
                        }
                    }
                }

                //System.out.println("[BOMB] ("+cell.col+", "+cell.row+") - " + "cell.neighboringBombs: " + cell.neighboringBombs + ", neighboringPossible.size(): " + neighboringPossible.size());

                if(neighboringPossible.size() == cell.neighboringBombs) {
                    for(Cell bomb : neighboringPossible) {
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

    // { { safe… }, { bomb… } }
    public ArrayList<ArrayList<Cell>> solveSituation() throws GuessRequiredException, GameWonException {

        int newlyFoundBombs = computeBombs();
        int newlyFoundSafe = computeSafe();

        //int iteration = 0;
        //System.out.println("["+iteration+"] Bombs: " + this.foundBombs.size() + ", Safe: " + this.foundSafe.size());
        while (newlyFoundBombs != 0 && newlyFoundSafe != 0) {
            newlyFoundBombs = this.computeBombs();
            newlyFoundSafe = this.computeSafe();

            //System.out.println("["+iteration+"] Bombs: " + currentFoundBombs + ", Safe: " + currentFoundSafe);
            //iteration++;
        }

        // if solved situation has no newly found safe cells 
        // and there are no more safe cells to be revealed
        if (newlyFoundSafe == 0 && newlyFoundBombs == 0 && this.unrevealedSafe <= 0) {
            if (this.foundUnknown.size() == 0) {
                throw new GameWonException();
            }
            throw new GuessRequiredException(this.foundUnknown.size(), this.unrevealedSafe);
        }

        ArrayList<ArrayList<Cell>> solvedSituation = new ArrayList<ArrayList<Cell>>();
        solvedSituation.add(this.foundSafe);
        solvedSituation.add(this.foundBombs);

        return solvedSituation;
    }

    Solver(Game game) {
        this.game = game;
        this.unrevealedSafe = 0;

        this.foundBombs = new ArrayList<Cell>();
        this.foundSafe = new ArrayList<Cell>();
        this.foundUnknown = new ArrayList<>();
    }
}
