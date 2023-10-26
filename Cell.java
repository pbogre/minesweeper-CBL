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

    /**
     * The markUnknown function sets the background color of the cell to yellow if it's unsure.
     */
    void markUnknown() {
        this.setBackground(Color.YELLOW);
    }

    /**
     * The markSafe() function sets the background color of the cell to green if it is clear.
     */
    void markSafe() {
        this.setBackground(Color.GREEN);
    }

    /**
     * The markBomb() function changes the background color to red if it is a bomb.
     */
    void markBomb() {
        this.setBackground(Color.RED);
    }

    /**
     * The function "makeBomb" sets the "isBomb" variable to true.
     */
    void makeBomb() {
        this.isBomb = true;
    }

    /**
     * The function sets the number of neighboring bombs and updates the icon of the cell based on the
     * number of neighboring bombs.
     * 
     * @param neighboringBombs The parameter `neighboringBombs` represents the number of neighboring
     * bombs around a particular cell in a game.
     */
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

    /**
     * The `reveal` function updates the appearance of a component based on its properties, such as
     * setting an icon if it represents a bomb or changing the background color and border.
     */
    void reveal() {
        this.isRevealed = true;

        if(this.isBomb) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/res/bomb.png"));
            setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_FAST)));
            return;
        }

        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    /**
     * The function toggles a flag on a cell and updates its icon accordingly.
     */
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

    // The `Cell(int row, int col)` constructor is initializing a new instance of the `Cell` class with
    // the given `row` and `col` parameters.
    Cell(int row, int col) {
        this.row = row;
        this.col = col;

        setBackground(new Color(180, 180, 180));
        setBorder(BorderFactory.createRaisedBevelBorder());

        setText("");
    }

    /**
     * The equals() function checks if two Cell objects have the same row and column values.
     * 
     * @param obj The "obj" parameter is an object that is being compared to the current object for
     * equality.
     * @return The method is returning a boolean value, which indicates whether the current object is
     * equal to the object being compared.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Cell cell = (Cell) obj;
        return row == cell.row &&
               col == cell.col;
    }

    /**
     * The hashCode() function in Java returns the hash code value for the object based on the values
     * of the row and col variables.
     * 
     * @return The method is returning the hash code of the combination of the "row" and "col"
     * variables.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
