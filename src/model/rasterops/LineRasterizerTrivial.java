package model.rasterops;

import model.rasterdata.Raster;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
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

        double dx = (double) (x2 - x1);
        double dy = (double) (y2 - y1);

        // pokud je úsečka více vodorovná
        if (Math.abs(dx) > Math.abs(dy)) {
            // zajisti průchod od menšího x k většímu
            if (x1 > x2) {
                int tempX = x1;
                x1 = x2;
                x2 = tempX;

                int tempY = y1;
                y1 = y2;
                y2 = tempY;

                dx = (double) (x2 - x1);
                dy = (double) (y2 - y1);
            }

            double k = dy / dx;
            double q = y1 - k * x1;

            for (int x = x1; x <= x2; x++) {
                int y = (int) Math.round(k * x + q);
                raster.setPixel(x, y, color.getRGB());
            }
        } else { // více svislá úsečka
            // zajisti průchod od menšího y k většímu
            if (y1 > y2) {
                int tempX = x1;
                x1 = x2;
                x2 = tempX;
                int tempY = y1;
                y1 = y2;
                y2 = tempY;
                dx = (double) (x2 - x1);
                dy = (double) (y2 - y1);
            }

            double k = dx / dy;
            double q = x1 - k * y1;

            for (int y=y1; y <= y2; y++) {
                int x = (int) Math.round(k * y + q);
                raster.setPixel(x, y, color.getRGB());
            }
        }
    }
}
