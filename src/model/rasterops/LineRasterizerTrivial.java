package model.rasterops;

import model.rasterdata.Raster;

/**
 * Triviální algoritmus
 *
 * Popis:
 * - založený na parametrickém vyjádření úsečky
 * - vypočítá směrnici a prochází úsečku po malých krocích podle větší z hodnot
 * - pro každý krok se určí souřadnice x a y pomocí přímky a vykreslí se pixel
 * - používá reálná (desetinná) čísla, která se zaokrouhlují na nejbližší celočíselnou pozici v rastru
 *
 * Výhody:
 * - jednoduchý na pochopení a implementaci
 * - přesná poloha pixelů umožňuje snadno doplnit interpolaci barev nebo dalších hodnot
 * - použitelný i pro složitější křivky
 *
 * Nevýhody:
 * - pomalejší než Bresenhamův, protože využívá desetinné operace
 * - může způsobovat drobné chyby zaokrouhlení
 * - je to funkce, tzn. jednomu x odpovídá právě jedno y, svislá úsečka se tedy řeší zvlášť
 * - není efektivní pro reálné aplikace, používá se hlavně pro demonstraci principu rasterizace
 */

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 == y2) { // degenerovaná úsečka
            raster.setPixel(x1, y1, color.getRGB());
            return;
        }

        float dx = x2 - x1;
        float dy = y2 - y1;

        // určení orientace úsečky - pokud je více vodorovná nebo svislá
        if (Math.abs(dx) > Math.abs(dy)) {
            // úsečka je víc vodorovná = řídící osa x
            // prohodí koncové body, aby se kreslilo zleva doprava
            if (x1 > x2) {
                int tempX = x1;
                x1 = x2;
                x2 = tempX;

                int tempY = y1;
                y1 = y2;
                y2 = tempY;

                dx = x2 - x1;
                dy = y2 - y1;
            }

            float k = dy / dx;
            float q = y1 - k * x1;

            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q); // zaokrouhlení na nejbližší pixel
                raster.setPixel(x, y, color.getRGB());
            }
        } else {
            // úsečka je více svislá = řídící osa y
            // prohodí koncové body, aby se kreslilo shora dolů
            if (y1 > y2) {
                int tempX = x1;
                x1 = x2;
                x2 = tempX;

                int tempY = y1;
                y1 = y2;
                y2 = tempY;

                dx = x2 - x1;
                dy = y2 - y1;
            }

            float k = dx / dy;
            float q = x1 - k * y1;

            for (int y = y1; y <= y2; y++) {
                int x = Math.round(k * y + q);
                raster.setPixel(x, y, color.getRGB());
            }
        }
    }
}
