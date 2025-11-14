package model.rasterops.rasterizer;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

/**
 * Bresenhamův algoritmus
 * S podporou tlustých čar (efekt kapsle)
 *
 * Popis:
 * - postupně prochází osu x nebo y od počátečního bodu k cílovému
 * - pro každý krok rozhoduje, zda se má posunout o 1 pixel nahoru/dolů nebo zůstat na stejné řadě
 * - to dělá na základě chybové hodnoty, která určuje, jak daleko je aktutální bod od ideální úsečky
 * - celý výpočet probíhá pouze pomocí celých čísel
 *
 * Výhody:
 * - je rychlý - využívá jen celočíselné sčítání a odčítání
 * - jednoduchá implementace - funguje na principu krokového rozhodování
 *
 * Nevýhody:
 * - neřeší anti-aliasing - úsečky jsou ostré bez vyhlazení hran
 * - obtížnější rozšíření pro barevné gradienty, jelikož se barva musí interpolovat zvlášť
 * - pro plnění oblastí by bylo potřeba kombinovat s jinými algoritmy
 */

public class LineRasterizerBresenham extends LineRasterizer {

    public LineRasterizerBresenham(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1; // směr po x (napravo/doleva)
        int sy = y1 < y2 ? 1 : -1; // směr po y (dolů/nahoru)

        int error = dx - dy; // počáteční chyba (odchylka) od ideální úsečky

        int thickness = 3; // tloušťka čáry
        int radius = thickness / 2; // poloměr kruhu kolem každého pixelu (kapsle)

        while (true) {
            // vykreslí kruh kolem aktuálního bodu, aby byla čára silná
            drawCircleCapsule(x1, y1, radius);

            if (x1 == x2 && y1 == y2) break; // konec při dosažení koncového bodu

            int e2 = 2 * error;

            // posun po x
            if (e2 > -dy) {
                error = error - dy;
                x1 = x1 + sx;
            }

            // posun po y
            if (e2 < dx) {
                error = error + dx;
                y1 = y1 + sy;
            }
        }
    }

    /**
     * Metoda kreslící kruhy kolem každého bodu čáry (pixelu), aby vzniklá souvislá silná čára
     * @param x x-ová souřadnice středu kruhu
     * @param y y-ová souřadnice středu kruhu
     * @param radius poloměr kruhu (polovina tloušťky čáry)
     */
    private void drawCircleCapsule(int x, int y, int radius) {
        // ohraničuje čtverec kolem středu kruhu
        int minX = x - radius;
        int maxX = x + radius;
        int minY = y - radius;
        int maxY = y + radius;

        // prochází všechny pixely
        for (int py = minY; py <= maxY; py++) {
            for (int px = minX; px <= maxX; px++) {
                if (px < 0 || py < 0 || px >= raster.getWidth() || py >= raster.getHeight()) continue;

                // vzdálenost od středu kruhu
                Point2D p = new Point2D(px, py);
                Point2D center = new Point2D(x, y);
                double dist = p.distanceTo(center);

                // pokud je uvnitř kruhu, vykreslí pixel
                if (dist <= radius) {
                    raster.setPixel(px, py, color.getRGB());
                }
            }
        }
    }
}
