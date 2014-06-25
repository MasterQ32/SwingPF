package de.mq32.swingpf;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Felix on 25.06.2014.
 */
public class TestFrame extends JFrame {
    public TestFrame() {
        this.setTitle("Title");
        this.setSize(new Dimension(300, 200));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton button = new JButton();
        button.setText("Kekse?");

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu();
        menu.setText("File");

        this.add(button);
    }
}
