package controller;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterdata.RasterBI;
import model.rasterops.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller2D implements Controller {

    private final Panel panel;
    private LineRasterizer liner;
    private PolygonRasterizer polygonRasterizer;
    private boolean isRasterizingWithShift = false;
    private boolean useWu = true;

    private Polygon polygon = new Polygon();
    private List<Line> lines;
    private Color polygonColor = Color.BLUE;
    private Color previewColor = Color.RED;

    // úprava tažením
    private int draggedPointIndex = -1; // žádný bod není vybraný
    private boolean isDragging = false;

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
        // netřeba inicializovat žádné objekty na začátku
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D newPoint = new Point2D(e.getX(), e.getY());

                // levé tlačítko - přidání prvního bodu polygonu
                if (polygon.size() == 0) {
                    polygon.addItem(newPoint);
                    drawScene();
                }

                // pravé tlačítko - najde nejbližší bod pro tažení
                if (e.getButton() == MouseEvent.BUTTON3) {
                    Point2D[] points = new Point2D[polygon.size()];
                    for (int i = 0; i < polygon.size(); i++) {
                        points[i] = polygon.getItem(i);
                    }
                    draggedPointIndex = polygon.findNearestPoint(newPoint, points);
                    isDragging = true;
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDragging) {
                    isDragging = false;
                    draggedPointIndex = -1;
                    drawScene();
                    return;
                }

                Point2D newPoint = new Point2D(e.getX(), e.getY());
                Line tempLine = new Line(polygon.getLast(), newPoint);

                if (isRasterizingWithShift) {
                    Point2D suitablePoint = findSuitablePoint(tempLine, polygon);
                    polygon.addItem(suitablePoint);

                } else {
                    polygon.addItem(newPoint);
                }
                lines.add(tempLine);
                drawScene();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawScene();

                // změna pozice vybraného vrcholu
                if(isDragging && draggedPointIndex != -1) {
                    Point2D dragged = polygon.getItem(draggedPointIndex);
                    dragged.setX(e.getX());
                    dragged.setY(e.getY());
                    return; // když se dragguje vrchol, nechceme pružnou čáru
                }

                if(polygon.size() < 1) return;

                // vykreslení pružné dashed čáry
                Point2D newPoint = new Point2D(e.getX(), e.getY());
                DashedLineRasterizer dashedLiner = new DashedLineRasterizer(panel.getRaster());
                dashedLiner.setColor(previewColor);
                dashedLiner.setDashPattern(6, 4);
                dashedLiner.setGradient(Color.BLUE, Color.RED); // použití gradientu

                Line tempLine = new Line(polygon.getLast(), newPoint);

                if (isRasterizingWithShift) {
                    dashedLiner.rasterize(new Line(polygon.getFirst(), findSuitablePoint(tempLine, polygon)));
                    dashedLiner.rasterize(new Line(polygon.getLast(), findSuitablePoint(tempLine, polygon)));
                } else {
                    dashedLiner.rasterize(new Line(polygon.getFirst(), newPoint));
                    dashedLiner.rasterize(new Line(polygon.getLast(), newPoint));
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // vymazání plátna
                    case KeyEvent.VK_C:
                        panel.clear();
                        polygon.clear();
                        lines.clear();
                        panel.repaint();
                        break;

                    // vykreslování úseček podle 3 typů směrů
                    case KeyEvent.VK_SHIFT:
                        isRasterizingWithShift = true;
                        break;

                    // přidání nového vrcholu do nejbližší hrany
                    case KeyEvent.VK_N:
                        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                        SwingUtilities.convertPointFromScreen(mousePosition, panel);
                        Point2D cursor = new Point2D(mousePosition.getX(), mousePosition.getY());

                        int insertIndex = Polygon.findNearestLine(cursor, polygon);
                        if (insertIndex != -1) {
                            polygon.addItemToIndex(insertIndex + 1, cursor);
                            drawScene();
                        }
                        break;

                    // smaže nejbližší vrchol
                    case KeyEvent.VK_R:
                        Point mousePosition2 = MouseInfo.getPointerInfo().getLocation();
                        SwingUtilities.convertPointFromScreen(mousePosition2, panel);
                        Point2D cursor2 = new Point2D(mousePosition2.getX(), mousePosition2.getY());

                        Point2D[] points = new Point2D[polygon.size()];

                        for (int i = 0; i < polygon.size(); i++) {
                            points[i] = polygon.getItem(i);
                        }

                        int nearestIndex = polygon.findNearestPoint(cursor2, points);

                        if (nearestIndex != -1) {
                            polygon.removeItem(nearestIndex);
                            drawScene();
                        }
                        break;

                    // přepnutí na algoritmus Xiolin Wu pro anti-aliasing
                    case KeyEvent.VK_A:
                        toggleWuAlgorithm();
                        break;

                    // změna pozadí
                    case KeyEvent.VK_B:
                        toggleBackground();
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

        // nalezení nejbližšího z těchto bodů k pozici kurzoru
        Point2D[] candidates = { horizontal, vertical, diagonal };
        int nearestIndex = polygon.findNearestPoint(cursor, candidates);
        Point2D nearest = candidates[nearestIndex];

        return nearest;
    }

    private void toggleWuAlgorithm() {
        Color currentColor = polygonColor;
        if (useWu) {
            liner = new LineRasterizerXiaolinWu(panel.getRaster());
        } else {
            liner = new FilledLineRasterizer(panel.getRaster());
        }
        liner.setColor(currentColor);
        useWu = !useWu;

        //  překreslí polygon a všechny čáry
        polygonRasterizer = new PolygonRasterizer(liner);
        drawScene();
    }

    private void toggleBackground(){
        if(RasterBI.backgroundColor == Color.WHITE){
            RasterBI.backgroundColor = Color.BLACK;
        } else {
            RasterBI.backgroundColor = Color.WHITE;
        }
        drawScene();
    }

    private void drawScene() {
        panel.getRaster().clear();
        polygonRasterizer.rasterize(polygon);
        panel.repaint();
    }
}
