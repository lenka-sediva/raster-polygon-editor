package model.rasterops;

import model.rasterdata.Raster;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        double k = (double) (y2 - y1) / (x2 - x1);
        double q = y1 - k * x1;
        for (int x = x1; x <= x2; x++) {
            int y = (int) (k * x + q);
            raster.setPixel(x, y, color.getRGB());
        }

        // 1. vypocitaj dx = (c2 - c1), dy = (y2 - y1)
        // 2. if abs(dx) > abs(dy) (iterate by x)
        // 3. if x1 > x2:
        // prohod (x1, r1) s (x2, y2) - draw(x2, y2, x1, y1)
        // else
        // double k = (double) (y2 - y1) / (x2 - x1);
        // double q = y1 - (k * x1);
        // for (int x = x1; x <= x2; x++) {
        //      int y = (int) (k * x + q);
        //      raster.setColor(x, y, color);
        // }
        // else
        // 4. if r1 > r2:
        // prohod (x1, y1) s (x2, y2)
        // stejnej algoritmus s prohozenim y and x
    }
}
