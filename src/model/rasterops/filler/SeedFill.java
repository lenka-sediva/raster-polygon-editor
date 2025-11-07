package model.rasterops.filler;

import model.rasterdata.Raster;
import model.objectdata.Point2D;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SeedFill {
    private final Raster raster;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    /**
     * Metoda záplavového algoritmu pro 4-spojitou i 8-spojitou oblast pomocí zásobníku
     * @param x x-ová souřadnice semínka
     * @param y y-ová souřadnice semínka
     * @param fillCol nová barva, jakou má být útvar vyplněný
     * @param pattern vzor výplně (pokud je null, použije se fillCol)
     * @param seedFill8 true, pokud chceme vyplňovat 8 spojitě
     */
    public void seedFillStack(int x, int y, int fillCol, PatternFill pattern, boolean seedFill8) {
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
                if(seedFill8) {
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
     * @param x x-ová souřadnice semínka
     * @param y y-ová souřadnice semínka
     * @param fillCol nová barva, jakou má být útvar vyplněný
     * @param seedFill8 true, pokud chceme vyplňovat 8 spojitě
     */
    public void seedFillQueue(int x, int y, int fillCol, boolean seedFill8) {
        int backgroundColor = raster.getPixel(x,y);

        if (backgroundColor == fillCol) {
            return;
        }

        Queue<Point2D> queue = new LinkedList<>();
        queue.add(new Point2D(x, y));

        while(!queue.isEmpty()) {
            Point2D p = queue.poll();
            x = p.getX();
            y = p.getY();

            if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight()) {
                continue;
            }

            int pixelColor = raster.getPixel(x, y);

            if (pixelColor == backgroundColor) {
                raster.setPixel(x, y, fillCol);

                queue.offer(new Point2D(x + 1, y));
                queue.offer(new Point2D(x - 1, y));
                queue.offer(new Point2D(x, y + 1));
                queue.offer(new Point2D(x, y - 1));

                if(seedFill8) {
                    queue.offer(new Point2D(x + 1, y + 1));
                    queue.offer(new Point2D(x - 1, y - 1));
                    queue.offer(new Point2D(x + 1, y - 1));
                    queue.offer(new Point2D(x - 1, y + 1));
                }
            }
        }
    }
}
