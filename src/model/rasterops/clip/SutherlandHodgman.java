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
                        out.add(Line.intersection(S, P, clipStart, clipEnd));
                    }
                    out.add(P); // přidán bod P, protože je uvnitř
                } else if (S_inside) {
                    // P je venku, ale S uvnitř => přidán průsečík (výstup z oblasti)
                    out.add(Line.intersection(S, P, clipStart, clipEnd));
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
}