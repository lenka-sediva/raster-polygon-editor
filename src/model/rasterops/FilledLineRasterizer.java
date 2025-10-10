package model.rasterops;

import model.rasterdata.Raster;

public class FilledLineRasterizer extends LineRasterizerBresenham{
    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        super.drawLine(x1, y1, x2, y2);
    }

    // Bresenhamův algoritmus
    // výhody
    // nevýhody

}
