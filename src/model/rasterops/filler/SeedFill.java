package model.rasterops.filler;

import model.rasterdata.Raster;
import model.objectdata.Point2D;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static model.rasterdata.RasterBI.getBackgroundColor;

public class SeedFill implements Filler {
    private final Raster raster;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void fill(int x, int y, int newCol) {
        seedFill4Stack(x, y, newCol);
    }

    /**
     * Metoda záplavového algoritmu pro 4-spojitou oblast pomocí zásobníku
     * @param x x-ová souřadnice semínka
     * @param y y-ová souřadnice semínka
     * @param fillCol nová barva, jakou má být útvar vyplněný
     */
    public void seedFill4Stack(int x, int y, int fillCol) {
        // získej barvu pozadí z počátečního bodu
        int backgroundColor = raster.getPixel(x, y);

        // pokud je počáteční bod už vyplněný novou barvou nebo není pozadí, konec
        if (backgroundColor == fillCol || backgroundColor != getBackgroundColor().getRGB()) {
            return;
        }

        Stack<Point2D> stack = new Stack<>();
        stack.push(new Point2D(x, y)); // startovní pixel (semínko)

        while(!stack.isEmpty()) {
            Point2D p = stack.pop();
            x = p.getX();
            y = p.getY();

            // Kontrola hranic
            if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight())
                continue;

            // kontrola barvy pozadí
            if (raster.getPixel(x, y) == backgroundColor) {
                raster.setPixel(x, y, fillCol);

                // přidání 4 sousedů
                stack.push(new Point2D(x + 1, y));
                stack.push(new Point2D(x - 1, y));
                stack.push(new Point2D(x, y + 1));
                stack.push(new Point2D(x, y - 1));
            }
        }
    }

    /**
     * Metoda záplavového algoritmu pro 4-spojitou oblast pomocí fronty
     * @param x x-ová souřadnice semínka
     * @param y y-ová souřadnice semínka
     * @param fillCol nová barva, jakou má být útvar vyplněný
     */
    public void seedFill4Queue(int x, int y, int fillCol) {
        int backgroundColor = raster.getPixel(x,y);

        if (backgroundColor == fillCol || backgroundColor != getBackgroundColor().getRGB()) {
            return;
        }

        Queue<Point2D> queue = new LinkedList<>();
        queue.add(new Point2D(x, y)); // startovní pixel = semínko

        while(!queue.isEmpty()) {
            Point2D p = queue.poll();
            x = p.getX();
            y = p.getY();

            // kontrola hranic
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
            }
        }
    }
}
