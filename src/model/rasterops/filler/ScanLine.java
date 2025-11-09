package model.rasterops.filler;

import model.objectdata.Line;
import model.objectdata.Polygon;
import model.rasterops.rasterizer.PolygonRasterizer;
import model.rasterdata.Raster;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScanLine implements Filler {
    private final Polygon polygon;
    private final PolygonRasterizer polygonRasterizer;
    private final Raster raster;
    private PatternFill pattern;

    public ScanLine(Raster raster, Polygon polygon, PolygonRasterizer polygonRasterizer) {
        this.polygon = polygon;
        this.polygonRasterizer = polygonRasterizer;
        this.raster = raster;
    }

    // nastavení vzoru pro fill
    public void setPattern(PatternFill pattern) {
        this.pattern = pattern;
    }

    @Override
    public void fill() {
        scanLineFill(pattern);
    }

    /**
     * Vyplní polygon pomocí scanline algoritmu podle zadaného vzoru
     * @param pattern instance PatternFill, určuje barvu každého bodu
     */
    public void scanLineFill(PatternFill pattern) {
        List<Line> edges = new ArrayList<>(); // seznam hran (úseček)

        // projde všechny hrany polygonu a vodorovné ignoruje (neovlivňují průsečíky scanline)
        for (int i = 0; i < polygon.size(); i++) {
            Line edge = new Line(polygon.getItem(i), polygon.getItem((i + 1) % polygon.size()));
            if (!edge.isHorizontal()) {
                edge.orientate();
                edges.add(edge);
            }
        }

        if (edges.isEmpty()) return;

        // nalezení minimální a maximální y souřadnice polygonu
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;

        for (Line edge : edges) {
            if (edge.getStart().getY() < yMin) yMin = edge.getStart().getY();
            if (edge.getEnd().getY() > yMax) yMax = edge.getEnd().getY();
        }

        // projde všechny scanline řádky od yMin do yMax
        for(int y = yMin; y <= yMax; y++) {
            List<Integer> intersections = new ArrayList<>(); // seznam průsečíků

            // zjistí průsečíky aktuální scanline s každou hranou
            for(Line edge : edges) {
                if (edge.intersectionExists(y)) {
                    intersections.add(edge.getIntersection(y));
                }
            }

            sortIntersections(intersections); // seřazení průsečíků zleva doprava

            // vyplnění mezi průsečíky - vždy mezi lichým a sudým
            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                int xStart = intersections.get(i);
                int xEnd = intersections.get(i + 1);

                // pro každý pixel mezi průsečíky získá barvu ze vzoru a nastaví ji do rastrového bufferu
                for (int x = xStart; x <= xEnd; x++) {
                    Color c = pattern.getColor(x, y);
                    raster.setPixel(x, y, c.getRGB());
                }
            }
        }
        // obtáhnutí hranice polygonu
        polygonRasterizer.rasterize(polygon);
    }

    /**
     * Implementace algoritmu pro řazení průsečíků pomocí insertion sort
     * Řadí hodnoty v seznamu zleva doprava (vzestupně)
     * @param list seznam x-ových souřadnic průsečíků, který se upraví na seřazený
     */
    private void sortIntersections(List<Integer> list) {
        // projde všechny prvky od druhého k poslednímu
        for (int i = 1; i < list.size(); i++) {
            int key = list.get(i); // aktuální hodnota k vložení
            int j = i - 1;

            // posouvá všechny prvky větší než key o 1 místo doprava
            while (j >= 0 && list.get(j) > key) {
                list.set(j + 1, list.get(j));
                j--;
            }

            // vloží key na správnou pozici
            list.set(j + 1, key);
        }
    }
}
