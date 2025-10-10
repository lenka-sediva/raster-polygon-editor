package controller;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerBresenham;
import model.rasterops.PolygonRasterizer;
import view.Panel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller2D implements Controller {

    private final Panel panel;
    private LineRasterizer liner;

    private Polygon polygon = new Polygon();
    private List<Line> lines;
    private Color color;

    public Controller2D(Panel panel) {
        this.panel = panel;

        liner = new LineRasterizerBresenham(panel.getRaster());
        liner.setColor(color);

        this.lines = new ArrayList<>();

        initObjects();
        initListeners(panel);
    }

    @Override
    public void initObjects() {
        // TODO
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D newPoint = new Point2D(e.getX(), e.getY());
                polygon.addItem(newPoint);

                Point2D firstPoint = polygon.getFirst();
                Point2D lastPoint = polygon.getLast();
                int size = polygon.size();

                if (size > 1) {
                    // spoj s předchozím bodem
                    Point2D previousPoint = polygon.getItem(size -2);
                    lines.add(new Line(previousPoint, lastPoint));
                }

                // pokud máme alespoň 3 body, uzavři polygon
                if (size >= 3) {
                    if (!lines.isEmpty()) {
                        lines.remove(lines.size() - 1);
                    }
                    lines.add(new Line(lastPoint, firstPoint));
                }

                if (size < 2) {
                    drawScene();
                    return;
                }

                // přidej hrany mezi sousedními body
                for (int i = 0; i < size -1; i++) {
                    lines.add(new Line(polygon.getItem(i), polygon.getItem(i+1)));
                }

                // uzavři polygon: poslední -> první
                lines.add(new Line(lastPoint, firstPoint));

                drawScene();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            // mazání
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_C) {
                    panel.clear();
                    polygon.clear();
                    lines.clear();
                    panel.repaint();
                }
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();

        for (Line line : lines)
            liner.rasterize(line);

        panel.repaint();
    }

}
