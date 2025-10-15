import controller.Controller2D;
import view.Window;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window(1000, 800);
            Controller2D controller = new Controller2D(window.getPanel());
            controller.updateRaster(window.getPanel().getRaster());
            controller.drawScene();
            // new Controller3D(window.getPanel());
            window.setVisible(true); // zobrazit okno
        });
    }
}
