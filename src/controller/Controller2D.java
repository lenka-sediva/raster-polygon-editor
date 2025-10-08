package controller;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerGraphics;
import model.rasterops.PolygonRasterizer;
import view.Panel;

import java.util.ArrayList;
import java.util.List;

public class Controller2D implements Controller {

    private final Panel panel;
    private LineRasterizer liner;

    public Controller2D(Panel panel) {
        this.panel = panel;

        initObjects();
        initListeners(panel);
    }

    @Override
    public void initObjects() {
        // TODO

        liner = new LineRasterizerGraphics(panel.getRaster());

        // Prikladove vykreslenie polygonu
        Line l1 = new Line(new Point2D(50,50), new Point2D(200, 20));
        Line l2 = new Line(new Point2D(200, 20), new Point2D(250, 130));
        Line l3 = new Line(new Point2D(250,130), new Point2D(400, 280));
        Line l4 = new Line(new Point2D(400,280), new Point2D(100, 280));
        Line l5 = new Line(new Point2D(100, 280), new Point2D(50, 50));

        List<Line> listofLines = new ArrayList<>();
        listofLines.add(l1);
        listofLines.add(l2);
        listofLines.add(l3);
        listofLines.add(l4);
        listofLines.add(l5);

        PolygonRasterizer polygonRasterizer = new PolygonRasterizer(liner);
        polygonRasterizer.rasterize(listofLines);

    }

    @Override
    public void initListeners(Panel panel) {
        // TODO

    }
}
