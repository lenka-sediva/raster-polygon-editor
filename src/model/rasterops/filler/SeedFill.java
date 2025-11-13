package model.rasterops.filler;

import model.rasterdata.Raster;
import model.objectdata.Point2D;

import java.util.*;

public class SeedFill implements Filler {
    private final Raster raster;
    private int startX;
    private int startY;
    private int fillCol;
    private int borderCol;
    private PatternFill pattern;
    private boolean fill8;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    // nastavení parametrů pro fill
    public void setSeed(int x, int y, int fillCol, int borderCol, PatternFill pattern, boolean fill8) {
        this.startX = x;
        this.startY = y;
        this.fillCol = fillCol;
        this.borderCol = borderCol;
        this.pattern = pattern;
        this.fill8 = fill8;
    }

    @Override
    public void fill() {
        if (borderCol == 0) { // není nastavena hranice
            seedFillStack(startX, startY, fillCol, pattern, fill8);
        } else {
            seedFillQueue(startX, startY, fillCol, borderCol, pattern, fill8);
        }
    }

    /**
     * Metoda záplavového algoritmu pro 4-spojitou i 8-spojitou oblast pomocí zásobníku
     * Omezující podle POZADÍ
     * @param x x-ová souřadnice semínka
     * @param y y-ová souřadnice semínka
     * @param fillCol nová barva, jakou má být útvar vyplněný
     * @param pattern vzor výplně (pokud je null, použije se fillCol)
     * @param fill8 true, pokud chceme vyplňovat 8 spojitě
     */
    public void seedFillStack(int x, int y, int fillCol, PatternFill pattern, boolean fill8) {
        // získá barvu pozadí v místě kliknutí (semínka)
        int backgroundColor = raster.getPixel(x, y);

        // konec při vyplnění
        if (backgroundColor == fillCol) {
            return;
        }

        Stack<Point2D> stack = new Stack<>();
        stack.push(new Point2D(x, y)); // startovní pixel (semínko)

        while(!stack.isEmpty()) {
            Point2D p = stack.pop();
            x = p.getX();
            y = p.getY();

            // kontrola hranic
            if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight()) {
                continue;
            }

            // barva vyplnění
            int targetColor = (pattern == null) ? fillCol : pattern.getColor(x,y).getRGB();

            // kontrola barvy pozadí
            if (raster.getPixel(x, y) == backgroundColor) {
                raster.setPixel(x, y, targetColor);

                // přidání 4 sousedů
                stack.push(new Point2D(x + 1, y));
                stack.push(new Point2D(x - 1, y));
                stack.push(new Point2D(x, y + 1));
                stack.push(new Point2D(x, y - 1));

                // přidání diagonálních sousedů pro 8-spojitou oblast
                if(fill8) {
                    stack.push(new Point2D(x + 1, y + 1));
                    stack.push(new Point2D(x - 1, y - 1));
                    stack.push(new Point2D(x + 1, y - 1));
                    stack.push(new Point2D(x - 1, y + 1));
                }
            }
        }
    }

    /**
     * Metoda záplavového algoritmu pro 4-spojitou i 8-spojitou oblast pomocí fronty
     * Omezující podle HRANICE
     * @param x x-ová souřadnice semínka
     * @param y y-ová souřadnice semínka
     * @param fillCol nová barva, jakou má být útvar vyplněný
     * @param borderCol barva hranice, kterou má být útvar omezený
     * @param pattern vzor výplně (pokud je null, použije se fillCol)
     * @param fill8 true, pokud chceme vyplňovat 8 spojitě
     */
    public void seedFillQueue(int x, int y, int fillCol, int borderCol, PatternFill pattern, boolean fill8) {
        int width = raster.getWidth();
        int height = raster.getHeight();

        int startColor = raster.getPixel(x, y); // semínko

        // omezení hranicí
        if (startColor == borderCol) return;

        Queue<Point2D> queue = new LinkedList<>();
        queue.add(new Point2D(x, y));

        while(!queue.isEmpty()) {
            Point2D p = queue.poll();
            x = p.getX();
            y = p.getY();

            if (x < 0 || y < 0 || x >= width || y >= height) {
                continue;
            }

            int pixelColor = raster.getPixel(x, y);

            // pokud je pixel už vyplněn
            if (pixelColor == borderCol) continue;

            // pokud vyplňujeme vzorem, vynecháme všechny pixely, které nejsou startColor
            if (pattern != null && pixelColor != startColor) continue;

            int targetColor = (pattern == null) ? fillCol : pattern.getColor(x, y).getRGB();
            raster.setPixel(x, y, targetColor);

            queue.offer(new Point2D(x + 1, y));
            queue.offer(new Point2D(x - 1, y));
            queue.offer(new Point2D(x, y + 1));
            queue.offer(new Point2D(x, y - 1));

            if(fill8) {
                queue.offer(new Point2D(x + 1, y + 1));
                queue.offer(new Point2D(x - 1, y - 1));
                queue.offer(new Point2D(x + 1, y - 1));
                queue.offer(new Point2D(x - 1, y + 1));
            }
        }
    }
}
