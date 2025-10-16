package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window(int width, int height) {
        panel = new Panel(width, height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setTitle("PGRF1 2025: Task 1 by Lenka Šedivá");

        add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);

        createControlPanel();

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice
    }

    // uživatelská nápověda
    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setBackground(Color.LIGHT_GRAY);

        Font bigFont = new Font("SansSerif", Font.BOLD, 16);
        UIManager.put("Label.font", bigFont); // nastaví výchozí font pro všechny JLabel

        JLabel helpLabel = new JLabel("Nápověda");
        helpLabel.setForeground(Color.WHITE); // bílý text
        helpLabel.setBackground(new Color(30, 144, 255)); // modré pozadí (DogerBlue)
        helpLabel.setOpaque(true);
        helpLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.add(helpLabel, BorderLayout.NORTH);

        // panel pro 2 sloupce pod nápovědou
        JPanel columnsPanel = new JPanel();
        columnsPanel.setLayout(new GridLayout(1, 2));
        columnsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(new JLabel("Přidání nového vrcholu: levé tlačítko myši"));
        leftPanel.add(new JLabel("Změna barvy pozadí: [B]"));
        leftPanel.add(new JLabel("Přidání vrcholu na nejbližší hranu: [N]"));
        leftPanel.add(new JLabel("Vymazání plátna: [C]"));
        leftPanel.add(new JLabel("Změna barvy: [F]"));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(new JLabel("Posunutí vrcholu: pravé tlačítko myši"));
        rightPanel.add(new JLabel("Zobrazování úseček: [SHIFT]"));
        rightPanel.add(new JLabel("Smazání nejbližšího vrcholu: [R]"));
        rightPanel.add(new JLabel("Anti-aliasing: [A]"));

        columnsPanel.add(leftPanel);
        columnsPanel.add(rightPanel);

        controlPanel.add(columnsPanel, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.NORTH);
    }

    public Panel getPanel() {
        return panel;
    }
}
