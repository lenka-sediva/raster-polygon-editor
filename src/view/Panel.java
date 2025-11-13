package view;

import model.rasterdata.Raster;
import model.rasterdata.RasterBI;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private Raster raster;
    private String drawModeText = "";
    private String drawModeShortcut = "";
    private String patternText = "";
    private String fillModeText = "";

    public Panel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        raster = new RasterBI(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString(fillModeText, 10, getHeight() - 60);
        g.drawString(drawModeText, 10, getHeight() - 35);
        g.drawString(patternText, 10, getHeight() - 10);
        g.drawString(drawModeShortcut, getWidth() - 125, getHeight() - 10);
    }

    public void setFillModeText(String fillModeText) {
        this.fillModeText = fillModeText;
        repaint();
    }

    public void setDrawModeText(String drawModeText) {
        this.drawModeText = drawModeText;
        repaint();
    }

    public void setDrawModeShortcut(String drawModeShortcut) {
        this.drawModeShortcut = drawModeShortcut;
        repaint();
    }

    public void setPatternText(String patternText) {
        this.patternText = patternText;
        repaint();
    }

    public Raster getRaster() {
        return raster;
    }

    public void clear() {
        raster.clear();
    }

    public void setRaster(Raster newRaster) {
        this.raster = newRaster;
    }
}
