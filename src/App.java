import controller.Controller2D;
import view.Window;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window(800, 600);
            new Controller2D(window.getPanel());
            // new Controller3D(window.getPanel());
            window.setVisible(true); // zobrazit okno
        });


    }
}
