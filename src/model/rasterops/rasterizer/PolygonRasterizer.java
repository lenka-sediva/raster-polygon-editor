package model.rasterops.rasterizer;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;

import java.util.List;


/**
 *
 * @author KŠ
 * 06.10.2025 11:58
 */

public class PolygonRasterizer {

    private LineRasterizer liner;

    public PolygonRasterizer(LineRasterizer liner) {
        this.liner = liner;
    }

    public void setLiner(LineRasterizer liner) {
        this.liner = liner;
    }

    public void rasterize(List<Line> listOfLines){
        for(Line line : listOfLines) {
            liner.drawLine(
                    line.getStart().getX(),
                    line.getStart().getY(),
                    line.getEnd().getX(),
                    line.getEnd().getY()
            );
        }
    }

    public void rasterize(Polygon polygon) {
        for (int i = 0; i < polygon.size(); i++) {
            Point2D p1 = polygon.getItem(i); // zaciatocny bod usecky

            Point2D p2 = polygon.getItem((i + 1) % polygon.size()); // koncovy bod usecky
            // bacha na vykreslenie posledneho bodu polygonu, s modulom si zaistim navrat k prvemu bodu polygonu

            liner.drawLine(
                    p1.getX(), p1.getY(),
                    p2.getX(), p2.getY()
            );
        }
    }
}
