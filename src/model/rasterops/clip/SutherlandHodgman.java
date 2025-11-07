package model.rasterops.clip;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;

import java.util.ArrayList;
import java.util.List;

public class SutherlandHodgman {
    /**
     * Ořeže polygon pomocí konvexního ořezávacího polygonu
     * @param original původní polygon, který ořezáváme
     * @param cuttingPolygon ořezávací polygon (musí být konvexní)
     * @return nový polygon po ořezu
     */
    public Polygon clip(Polygon original, Polygon cuttingPolygon) {
        Polygon newPolygon = new Polygon();

        // převedení original polygonu na seznam bodů
        List<Point2D> in = new ArrayList<>();
        for (int i = 0; i < original.size(); i++) {
            in.add(original.getItem(i));
        }

        List<Point2D> out = new ArrayList<>();

        // pro každou hranu ořezávacího polygonu
        for (int i = 0; i < cuttingPolygon.size(); i++) {
            Point2D clipStart = cuttingPolygon.getItem(i);
            Point2D clipEnd = cuttingPolygon.getItem((i + 1) % cuttingPolygon.size());
            Line clipEdge = new Line(clipStart, clipEnd);

            out.clear();
            if (in.isEmpty()) break; // konec, pokud už není co ořezávat

            // poslední vrchol polygonu = S (start) pro kontrolu hran
            Point2D S = in.getLast();

            // pro každý bod ořezávaného polygonu
            for (Point2D P : in) {
                boolean P_inside = clipEdge.isInside(P);
                boolean S_inside = clipEdge.isInside(S);

                if (P_inside) {
                    if (!S_inside) {
                        // P vstupuje do oblasti => přidán průsečík
                        out.add(intersection(S, P, clipStart, clipEnd));
                    }
                    out.add(P); // přidán bod P, protože je uvnitř
                } else if (S_inside) {
                    // P je venku, ale S uvnitř => přidán průsečík (výstup z oblasti)
                    out.add(intersection(S, P, clipStart, clipEnd));
                }
                S = P; // posunutí S na aktuální bod P
            }

            // výstupní seznam se stává vstupem pro další hranu
            in = new ArrayList<>(out);
        }

        // převod výstupního seznamu zpět na Polygon
        for (Point2D p : in) {
            newPolygon.addItem(p);
        }
        return newPolygon;
    }

    /**
     * Vypočítá průsečík dvou úseček (S-P a A-B)
     * @param S počáteční bod 1. úsečky
     * @param P koncový bod 1. úsečky
     * @param A počáteční bod 2. úsečky (hranice ořezávacího polygonu)
     * @param B koncový bod 2. úsečky
     * @return bod průsečíku
     */
    private Point2D intersection(Point2D S, Point2D P, Point2D A, Point2D B) {
        double x1 = S.getX(), y1 = S.getY();
        double x2 = P.getX(), y2 = P.getY();
        double x3 = A.getX(), y3 = A.getY();
        double x4 = B.getX(), y4 = B.getY();

        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denom == 0) throw new IllegalArgumentException("Rovnoběžné hrany – nelze spočítat průsečík");

        double px = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / denom;
        double py = ((x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4)) / denom;

        return new Point2D(px, py);
    }
}