package model.rasterops;

import model.rasterdata.Raster;

/**
 * Třída FilledLineRasterizer vykresluje úsečky pomocí Bresenhamova algoritmu
 * Ten dědí ze třídy LineRasterizerBresenham, která je potomkem třídy LineRasterizer
 *
 * Jeho výhody:
 * - rychlý, protože používá pouze celočíselné operace
 * - sčítání a odčítání
 * - vykresluje úsečky přesně bez zaoblení (aliasing je minimální)
 * - dobře funguje pro všechny kvadranty a směr čáry
 *
 * Nevýhody:
 * - neřeší anti-aliasing (čára může být schodovitá)
 * - pro plnění oblastí by bylo potřeba kombinovat s jinými algoritmy
 */

public class FilledLineRasterizer extends LineRasterizerBresenham{
    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        super.drawLine(x1, y1, x2, y2);
    }
}
