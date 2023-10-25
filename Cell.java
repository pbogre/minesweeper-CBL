import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import java.awt.*;

import java.util.*;

public class Cell extends JButton {
    public final int row;
    public final int col;
    public int neighboringBombs;
    public boolean isBomb;
    public boolean isFlagged;
    public boolean isRevealed;

    void markUnknown() {
        this.setBackground(Color.YELLOW);
    }

    void markSafe() {
        this.setBackground(Color.GREEN);
    }

    void markBomb() {
        this.setBackground(Color.RED);
    }

    void makeBomb() {
        this.isBomb = true;
    }

    void setNeighboringBombs(int neighboringBombs) {
        ImageIcon icon;
        this.neighboringBombs = neighboringBombs;
        if(this.neighboringBombs != 0) {
            switch(neighboringBombs){
                case 1:
                    icon = new ImageIcon(getClass().getResource("/res/1.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                case 2:
                    icon = new ImageIcon(getClass().getResource("/res/2.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                case 3:
                    icon = new ImageIcon(getClass().getResource("/res/3.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                case 4:
                    icon = new ImageIcon(getClass().getResource("/res/4.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                case 5:
                    icon = new ImageIcon(getClass().getResource("/res/5.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                case 6:
                    icon = new ImageIcon(getClass().getResource("/res/6.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                case 7:
                    icon = new ImageIcon(getClass().getResource("/res/7.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                case 8:
                    icon = new ImageIcon(getClass().getResource("/res/8.png"));
                    setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
                    break;
                default:
                    return;
            }
        }
    }

    void reveal() {
        this.isRevealed = true;

        if(this.isBomb) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/res/bomb.png"));
            setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
            return;
        }

        setBackground(Color.GRAY);
        setBorder(BorderFactory.createLoweredBevelBorder());
    }

    void toggleFlag() {
        if(this.isRevealed){
            return;
        }

        if (!this.isFlagged) {
            this.isFlagged = true;

            ImageIcon icon = new ImageIcon(getClass().getResource("/res/flag.png"));
            setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
        }
        else if(this.isFlagged){
            this.isFlagged = false;
            setIcon(null);
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
