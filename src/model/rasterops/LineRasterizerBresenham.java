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

    }
}
