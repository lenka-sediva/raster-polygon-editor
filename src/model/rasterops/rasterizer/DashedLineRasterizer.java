package model.rasterops.rasterizer;

import model.rasterdata.Raster;

import java.awt.*;

/**
 * Třída DashedLineRasterizer vykresluje čárkované úsečky pomocí DDA algoritmu
 * Navíc podporuje gradient díky atributu gradient datového typu Gradient ze třídy LineRasterizer
 */

public class DashedLineRasterizer extends LineRasterizer {

    private int dashLength = 8; // počet pixelů pro čárku
    private int gapLength = 4; // počet pixelů pro mezeru

    public DashedLineRasterizer(Raster raster) {
        super(raster);
    }

    /**
     * Umožňuje flexibilně měnit vzhled čárkované čáry pro případné budoucí využití
     * @param dashLength počet pixelů vykreslených jako čárka
     * @param gapLength počet pixelů, které tvoří mezeru mezi čárkami
     */
    public void setDashPattern(int dashLength, int gapLength) {
        this.dashLength = dashLength;
        this.gapLength = gapLength;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            raster.setPixel(x1, y1, color.getRGB());
            return;
        }

        // inkrementy pro každý krok
        double xInc = dx / steps;
        double yInc = dy / steps;

        // počáteční pozice
        double x = x1;
        double y = y1;

        // délka celého vzoru (čárka + mezera)
        int patternLength = dashLength + gapLength;

        for (int i = 0; i <= steps; i++) {
            int posInPattern = i % patternLength;
            if (posInPattern < dashLength) { // vykreslí se čárka
                // použije se gradient, pokud je definován, jinak základní barva
                Color c = (gradient != null) ? gradient.getColor((double)i / steps) : color;
                raster.setPixel((int) Math.round(x), (int) Math.round(y), c.getRGB());
            }
            // jinak je mezera a pixel se nekreslí

            // posun na další krok
            x = x + xInc;
            y = y + yInc;
        }
    }
}
