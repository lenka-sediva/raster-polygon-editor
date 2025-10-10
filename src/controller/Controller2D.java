package controller;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterops.FilledLineRasterizer;
import model.rasterops.LineRasterizer;
import model.rasterops.PolygonRasterizer;
import view.Panel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller2D implements Controller {

    private final Panel panel;
    private LineRasterizer liner;
    private PolygonRasterizer polygonRasterizer;
    private boolean isRasterizingWithShift = false;

    private Polygon polygon = new Polygon();
    private List<Line> lines;
    private Color polygonColor = Color.YELLOW;
    private Color previewColor = Color.RED;

    public Controller2D(Panel panel) {
        this.panel = panel;

        liner = new FilledLineRasterizer(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(liner);
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
            // přidání nového bodu
            @Override
            public void mousePressed(MouseEvent e) {
                if (polygon.size() == 0) {
                    Point2D newPoint = new Point2D(e.getX(), e.getY());
                    polygon.addItem(newPoint);
                    drawScene();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point2D newPoint = new Point2D(e.getX(), e.getY());
                Line tempLine = new Line(polygon.getLast(), newPoint);

                if (isRasterizingWithShift) {
                    Point2D suitablePoint = findSuitablePoint(tempLine, polygon);
                    polygon.addItem(suitablePoint);
                    drawScene();
                } else {
                    polygon.addItem(newPoint);
                    drawScene();
                }
                panel.repaint();
            }
        });

        // pohyb myši
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // zobrazí dočasné čáry
                if(polygon.size() < 1) return;
                drawScene();

                Point2D newPoint = new Point2D(e.getX(), e.getY());
                Line tempLine = new Line(polygon.getLast(), newPoint);

                liner.setColor(previewColor);

                if(isRasterizingWithShift) {
                    liner.rasterize(new Line(polygon.getFirst(), findSuitablePoint(tempLine, polygon)));
                    liner.rasterize(new Line(polygon.getLast(), findSuitablePoint(tempLine, polygon)));
                } else {
                    liner.rasterize(new Line(polygon.getFirst(), new Point2D(e.getX(), e.getY())));
                    liner.rasterize(new Line(polygon.getLast(), new Point2D(e.getX(), e.getY())));
                }

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
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isRasterizingWithShift = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isRasterizingWithShift = false;
                }
            }
        });
    }

    private Point2D findSuitablePoint(Line line, Polygon polygon) {
        Point2D start = line.getStart();
        Point2D cursor = line.getEnd();

        // proměnné pro přehlednější počítání s Point2D diagonal
        double dx = cursor.getX() - start.getX();
        double dy = cursor.getY() - start.getY();
        double min = Math.min(Math.abs(dx), Math.abs(dy));

        // možné cílové body: vodorovný, svislý, diagonální (45°)
        Point2D horizontal = new Point2D(cursor.getX(), start.getY());
        Point2D vertical = new Point2D(start.getX(), cursor.getY());
        Point2D diagonal = new Point2D(start.getX() + Math.signum(dx) * min, start.getY() + Math.signum(dy) * min);

        // najití nejbližšího z těchto bodů k pozici kurzoru
        Point2D[] candidates = { horizontal, vertical, diagonal };
        int nearestIndex = polygon.findNearestPoint(cursor, candidates);
        Point2D nearest = candidates[nearestIndex];

        return nearest;
    }

    private void drawScene() {
        panel.getRaster().clear();
        polygonRasterizer.rasterize(polygon);
        panel.repaint();
    }
}
