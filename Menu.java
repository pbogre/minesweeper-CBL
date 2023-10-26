import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;

public class Menu extends JFrame {

    public int windowSize;
    public int selectedGridSize;
    public int selectedBombAmount;

    private JSlider gridSizeSlider;
    private JSlider bombAmountSlider;
    private JLabel gridSizeLabel;
    private JLabel bombAmountLabel;

    private ImageIcon menuIcon;


    /**
     * The function updates the labels for grid size and bomb amount based on the values of the
     * corresponding sliders.
     */
    public void updateCustomDifficulty() {
        this.gridSizeLabel.setText("Grid size: " + this.gridSizeSlider.getValue());
        this.selectedGridSize = gridSizeSlider.getValue();

        this.selectedBombAmount = (int)(this.selectedGridSize * this.selectedGridSize * (double)(this.bombAmountSlider.getValue() / 100.0));
        this.bombAmountLabel.setText("Bombs / Cells: " + this.bombAmountSlider.getValue() + "% (" + this.selectedBombAmount + " total)");
    }

    /**
     * The function sets the difficulty level by updating the selected grid size and bomb amount, and
     * adjusting the values of the gridSizeSlider and bombAmountSlider accordingly.
     * 
     * @param gridSize The gridSize parameter represents the size of the grid. It determines the number
     * of rows and columns in the grid.
     * @param bombAmount The bombAmount parameter represents the number of bombs that will be placed on
     * the game grid.
     */
    public void setDifficulty(int gridSize, int bombAmount) {
        this.selectedGridSize = gridSize;
        this.selectedBombAmount = bombAmount;

        this.gridSizeSlider.setValue(gridSize);
        this.bombAmountSlider.setValue((100 * bombAmount) / (gridSize * gridSize));
    }
    
   /**
    * This function sets up and displays a JFrame window with a specified size and title.
    */
    public void run() {
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(this.windowSize, this.windowSize); // Set the frame size
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    /**
     * The "stop" function hides and disposes of the current window.
     */
    public void stop() {
        setVisible(false);
        dispose();
    }

    // The above code is defining a class called "Menu" in Java. This class represents a menu screen
    // for a Minesweeper game. The constructor takes a parameter "windowSize" which determines the size
    // of the menu window.
    public Menu(int windowSize) {
        menuIcon = new ImageIcon(getClass().getResource("/res/logo.png"));
        setIconImage(menuIcon.getImage());
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e){
            System.out.println("Error setting look and feel");
        }

        this.windowSize = windowSize;
        // default grid size & bomb amount (medium difficulty)
        this.selectedGridSize = 20;
        this.selectedBombAmount = 60;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        // Add a big label for the title
        JLabel titleLabel = new JLabel("Minesweeper");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));

        JLabel selectDifficultyLabel = new JLabel("Select Difficulty");
        selectDifficultyLabel.setFont(new Font("Arial", Font.ITALIC, 18));

        JButton startGameButton = new JButton("Start Game");
        startGameButton.setFocusPainted(false);

        JButton easyDifficultyButton = new JButton("Easy");
        easyDifficultyButton.setFocusPainted(false);

        JButton mediumDifficultyButton = new JButton("Medium");
        mediumDifficultyButton.setFocusPainted(false);

        JButton hardDifficultyButton = new JButton("Hard");
        hardDifficultyButton.setFocusPainted(false);

        JButton customDifficultyButton = new JButton("Custom");
        customDifficultyButton.setFocusPainted(false);

        JPanel difficultyPanel = new JPanel();

        this.gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 5, 35, this.selectedGridSize);
        this.bombAmountSlider = new JSlider(JSlider.HORIZONTAL, 0, 99, (100 * this.selectedBombAmount) / (this.selectedGridSize * this.selectedGridSize));

        this.gridSizeLabel = new JLabel("Grid size: " + gridSizeSlider.getValue());
        this.bombAmountLabel = new JLabel("Bombs / Cells: " + bombAmountSlider.getValue() + "% (" + this.selectedBombAmount + " total)");

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(titleLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(startGameButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(selectDifficultyLabel, constraints);
        
        difficultyPanel.add(easyDifficultyButton);
        difficultyPanel.add(mediumDifficultyButton);
        difficultyPanel.add(hardDifficultyButton);
        difficultyPanel.add(customDifficultyButton);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(difficultyPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        gridSizeSlider.setMajorTickSpacing(10);
        gridSizeSlider.setMinorTickSpacing(1);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setPaintLabels(true);
        gridSizeSlider.setVisible(false);
        add(gridSizeSlider, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        gridSizeLabel.setVisible(false);
        add(gridSizeLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        bombAmountSlider.setMajorTickSpacing(33);
        bombAmountSlider.setPaintTicks(true);
        bombAmountSlider.setPaintLabels(true);
        bombAmountSlider.setVisible(false);
        add(bombAmountSlider, constraints);

        constraints.gridx = 0;
        constraints.gridy = 7;
        bombAmountLabel.setVisible(false);
        add(bombAmountLabel, constraints);

        this.setDifficulty(this.selectedGridSize, this.selectedBombAmount);

        Menu self = this;
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    // Start game and stop menu
                    Game game = new Game(self.selectedGridSize, self.selectedBombAmount);
                    game.run();
                    self.stop();
                }
        });

        // difficulty inputs event listeners
        easyDifficultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.setDifficulty(10, 15);
                self.updateCustomDifficulty();
            }
        });
        mediumDifficultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.setDifficulty(20, 60);
                self.updateCustomDifficulty();
            }
        });
        hardDifficultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                self.setDifficulty(35, 185);
                self.updateCustomDifficulty();
            }
        });
        customDifficultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    gridSizeSlider.setVisible(!gridSizeSlider.isVisible());
                    gridSizeLabel.setVisible(!gridSizeLabel.isVisible());
                    bombAmountSlider.setVisible(!bombAmountSlider.isVisible());
                    bombAmountLabel.setVisible(!bombAmountLabel.isVisible());
                }
        });
        gridSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                self.updateCustomDifficulty();
            }
        });
        bombAmountSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e){
                self.updateCustomDifficulty();
            }
        });
    }
}
