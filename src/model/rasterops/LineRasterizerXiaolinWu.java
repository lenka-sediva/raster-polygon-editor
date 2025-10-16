package model.rasterops;

import model.rasterdata.Raster;

/**
 * Xiaolin Wu – algoritmus pro vykreslování anti-aliasovaných úseček
 *
 * Popis:
 * - každý pixel má intenzitu (jas/průhlednost) podle toho, jak moc je pokrytý ideální čárou neboli v jaké je od ní vzdálenosti
 * - vykresluje dvojici pixelů (1 nad a 1 pod čarou) s odpovídající jasovou hodnotou
 *
 * Výhody:
 * - vyhlazené úsečky bez ostrých hran (anti-aliasing)
 * - vhodný pro zobrazení vektorové grafiky
 *
 * Nevýhody:
 * - výpočetně náročnější (pracuje s desetinnými čísly)
 * - složitější implementace než Bresenham
 */

public class LineRasterizerXiaolinWu extends LineRasterizer{

    public LineRasterizerXiaolinWu(Raster raster) {
        super(raster);
    }

    // fractional part
    private double fpart (double x) {
        return x - Math.floor(x);
    }

    // remaining fractional part
    private double rfpart (double x) {
        return 1 - fpart(x);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 == y2) { // degenerovaná úsečka
            raster.setPixel(x1, y1, 255);
            return;
        }

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        // otočení souřadnic, pokud je čára strmá
        if (steep) {
            int temp = x1;
            x1 = y1;
            y1 = temp;

            temp = x2;
            x2 = y2;
            y2 = temp;
        }

        // vždy kreslíme zleva doprava
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;

            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        double dx = x2 - x1;
        double dy = y2 - y1;
        double gradient = dy / dx; // sklon čáry

        // začátek úsečky
        double xEnd = Math.floor(x1 + 0.5);
        double yEnd = y1 + gradient * (xEnd - x1);
        double xGap = rfpart(x1 + 0.5);
        int xPixel1 = (int) xEnd;
        int yPixel1 = (int) Math.floor(yEnd);

        // 255 = maximální jas
        int c1 = (int) (rfpart(yEnd) * xGap * 255); // jas horního pixelu
        int c2 = (int) (fpart(yEnd) * xGap * 255); // jas dolního pixelu

        // vykreslení startovních pixelů
        if (steep) {
            raster.setPixel(yPixel1, xPixel1, c1);
            raster.setPixel(yPixel1 + 1, xPixel1, c2);
        } else {
            raster.setPixel(xPixel1, yPixel1, c1);
            raster.setPixel(xPixel1, yPixel1 + 1, c2);
        }

        // průsečík
        double intery = yEnd + gradient;

        // konec úsečky
        double xEnd2 = Math.floor(x2 + 0.5);
        double yEnd2 = y2 + gradient * (xEnd2 - x2);
        int xPixel2 = (int) xEnd2;
        int yPixel2 = (int) Math.floor(yEnd2);
        double xGap2 = fpart(x2 + 0.5);

        int c3 = (int) (rfpart(yEnd2) * xGap2 * 255);
        int c4 = (int) (fpart(yEnd2) * xGap2 * 255);

        if (steep) {
            raster.setPixel(yPixel2, xPixel2, c3);
            raster.setPixel(yPixel2 + 1, xPixel2, c4);
        } else {
            raster.setPixel(xPixel2, yPixel2, c3);
            raster.setPixel(xPixel2, yPixel2 + 1, c4);
        }

        // hlavní smyčka - přidá pixel nad i pod úsečku
        for (double x = xPixel1 + 1; x < xPixel2; x++) {
            int c5 = (int) (rfpart(intery) * 255);
            int c6 = (int) (fpart(intery) * 255);

            if (steep) {
                raster.setPixel((int) Math.floor(intery), (int) x, c5);
                raster.setPixel((int) Math.floor(intery) + 1, (int) x, c6);
            } else {
                raster.setPixel((int) x, (int) Math.floor(intery), c5);
                raster.setPixel((int) x, (int) Math.floor(intery) + 1, c6);
            }
            intery = intery + gradient; // posun na další sloupec/řádek
        }
    }
}