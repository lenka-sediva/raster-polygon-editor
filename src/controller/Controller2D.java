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
    private Color polygonColor = Color.YELLOW;
    private Color previewColor = Color.RED;

    public Controller2D(Panel panel) {
        this.panel = panel;

        liner = new LineRasterizerBresenham(panel.getRaster());
        liner.setColor(polygonColor);

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
        // kliknutí myši
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // přidání nového bodu
                Point2D newPoint = new Point2D(e.getX(), e.getY());
                polygon.addItem(newPoint);

                redrawPolygon();
            }
        });

        // pohyb myši
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // zobrazí dočasné čáry
                if(polygon.size() < 1) return;

                redrawPolygon();

                liner.setColor(previewColor);
                liner.rasterize(new Line(polygon.getFirst(), new Point2D(e.getX(), e.getY())));
                liner.rasterize(new Line(polygon.getLast(), new Point2D(e.getX(), e.getY())));

                liner.setColor(polygonColor);
                panel.repaint();
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

    private void redrawPolygon() {
        lines.clear(); // vymaž staré hrany

        int size = polygon.size();
        if (size < 2) {
            drawScene();
            return;
        }

        // přidání hran mezi sousedními body
        for (int i = 0; i < size - 1; i++) {
            lines.add(new Line(polygon.getItem(i), polygon.getItem(i + 1)));
        }

        // uzavření polygonu, pokud má alespoň 3 body
        if (size >= 3) {
            lines.add(new Line(polygon.getLast(), polygon.getFirst()));
        }
        drawScene();
    }

    private void drawScene() {
        panel.getRaster().clear();

        for (Line line : lines)
            liner.rasterize(line);

        panel.repaint();
    }

}
