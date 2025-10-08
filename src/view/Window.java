package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window(int width, int height) {
        panel = new Panel(width, height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("PGRF1 2025: Task 1 by Jmeno Prijmeni");

        add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);

        setLocationRelativeTo(null);

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice
    }

    public Panel getPanel() {
        return panel;
    }
}
