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
        ImageIcon icon;
        this.neighboringBombs = neighboringBombs;
        if(this.neighboringBombs != 0) {
            switch(neighboringBombs){
                //TODO: Add the additional 7 pictures/resources
                case 1:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                case 2:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                case 3:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                case 4:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                case 5:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                case 6:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                case 7:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                case 8:
                    icon = new ImageIcon(getClass().getResource("/res/Minesweeper_1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
                    break;
                default:
                    return;
            }
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
