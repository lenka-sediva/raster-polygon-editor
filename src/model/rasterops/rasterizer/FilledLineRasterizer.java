package model.rasterops.rasterizer;

import model.rasterdata.Raster;

/**
 * Třída FilledLineRasterizer vykresluje úsečky pomocí algoritmu Xiaolin Wu
 * Ten dědí ze třídy LineRasterizerXiaolinWu, která je potomkem třídy LineRasterizer
 */

public class FilledLineRasterizer extends LineRasterizerXiaolinWu {
    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        super.drawLine(x1, y1, x2, y2);
    }
}
