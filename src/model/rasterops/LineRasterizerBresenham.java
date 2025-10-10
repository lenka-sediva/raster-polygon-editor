package model.rasterops;

import model.rasterdata.Raster;

public class LineRasterizerBresenham extends LineRasterizer {

    public LineRasterizerBresenham(Raster raster) {
        super(raster);
    }

    // TODO
    // https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1; // směr po x
        int sy = y1 < y2 ? 1 : -1; // směr po y
        int err = dx - dy; // počáteční chyba

        while (true) {
            raster.setPixel(x1, y1, color.getRGB());

            if (x1 == x2 && y1 == y2) {
                break; // konec při dosažení koncového bodu
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx; // posun po x
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy; // posun po y
            }
        }
    }
}
