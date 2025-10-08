package view;

import model.rasterdata.Raster;
import model.rasterdata.RasterBI;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private final Raster raster;

    public Panel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        raster = new RasterBI(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
    }

    public Raster getRaster() {
        return raster;
    }

    public void clear() {
        raster.clear();
    }
}
