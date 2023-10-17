import javax.swing.*;

import java.awt.*;
import java.util.*;

public class Cell extends JButton {
    public final int row;
    public final int col;
    public int neighboringBombs;
    public boolean isBomb;
    public boolean isFlagged;
    public boolean isRevealed;

    void makeBomb() {
        this.isBomb = true;
        //setText("B");
    }

    void setNeighboringBombs(int neighboringBombs) {
        this.neighboringBombs = neighboringBombs;
        if(this.neighboringBombs != 0) {
            setText(String.valueOf(this.neighboringBombs));
        }
    }

    void reveal() {
        this.isRevealed = true;

        if(this.isBomb && !this.isFlagged) {
            setText("B");
            return;
        }

        if(!this.isFlagged){
            setBackground(Color.GRAY);
            setBorder(BorderFactory.createLoweredBevelBorder());
        }

        /*setIcon(new ImageIcon(new ImageIcon(
            "C:/TUe/Homework/MineSweeperCBL/minesweeper-CBL/res/Minesweeper_1png.png").getImage()
            .getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH)));*/
        // do button stuff...
    }

    void flag() {
        if (!this.isRevealed) {
            this.isFlagged = true;
            setText("F");
        }
    }

    Cell(int row, int col) {
        this.row = row;
        this.col = col;

        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createRaisedBevelBorder());

        setText("");
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
