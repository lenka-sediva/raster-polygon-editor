package controller;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterdata.Raster;
import model.rasterdata.RasterBI;
import model.rasterops.rasterizer.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller2D implements Controller {

    private final Panel panel;
    private LineRasterizer liner;
    private final PolygonRasterizer polygonRasterizer;
    private boolean isRasterizingWithShift = false;
    private boolean useWu = true;

    private final Polygon polygon = new Polygon();
    private final List<Line> lines; // ukládání úseček pro možné budoucí využití
    private Color polygonColor = Color.BLUE;
    private final DashedLineRasterizer dashedLiner;

    private int draggedPointIndex = -1; // žádný bod není vybraný pro tažení
    private boolean isDragging = false;

    public Controller2D(Panel panel) {
        this.panel = panel;

        liner = new FilledLineRasterizer(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(liner);
        liner.setColor(polygonColor);

        this.lines = new ArrayList<>();

        this.dashedLiner = new DashedLineRasterizer(panel.getRaster());
        dashedLiner.setGradient(Color.BLUE, Color.RED); // výchozí gradient

        initObjects();
        initListeners(panel);
    }

    @Override
    public void initObjects() {
        // netřeba inicializovat žádné objekty na začátku, možné budoucí využití
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                RasterBI newRaster = new RasterBI(panel.getWidth(), panel.getHeight());
                newRaster.clear();
                panel.setRaster(newRaster);
                updateRaster(newRaster);
                drawScene();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D newPoint = new Point2D(e.getX(), e.getY());

                // levé tlačítko - přidání prvního bodu polygonu (nutné, jinak nejde drag prvního bodu)
                if (polygon.size() == 0) {
                    polygon.addItem(newPoint);
                    drawScene();
                }

                // pravé tlačítko - najde nejbližší bod pro tažení (změna vrcholu)
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
                if (isDragging) { // konec dragování
                    isDragging = false;
                    draggedPointIndex = -1;
                    drawScene();
                    return;
                }

                // přidání nového vrcholu polygonu
                Point2D newPoint = new Point2D(e.getX(), e.getY());
                Line tempLine = new Line(polygon.getLast(), newPoint);

                if (isRasterizingWithShift) {
                    Point2D suitablePoint = polygon.findNearestPointToAxisCandidates(tempLine);
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
                    return;
                }

                // vykreslení pružné dashed čáry
                Point2D newPoint = new Point2D(e.getX(), e.getY());
                Line tempLine = new Line(polygon.getLast(), newPoint);

                dashedLiner.setGradient(polygonColor, Color.RED); // gradient vždy od vybrané barvy do červené

                if (isRasterizingWithShift) {
                    dashedLiner.rasterize(new Line(polygon.getFirst(), polygon.findNearestPointToAxisCandidates(tempLine)));
                    dashedLiner.rasterize(new Line(polygon.getLast(), polygon.findNearestPointToAxisCandidates(tempLine)));
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
                        Point2D cursor = getCursorPosition();
                        int insertIndex = Polygon.findNearestLine(cursor, polygon);
                        if (insertIndex != -1) {
                            polygon.addItemToIndex(insertIndex + 1, cursor);
                            drawScene();
                        }
                        break;

                    // smaže nejbližší vrchol
                    case KeyEvent.VK_R:
                        Point2D[] points = new Point2D[polygon.size()];

                        for (int i = 0; i < polygon.size(); i++) {
                            points[i] = polygon.getItem(i);
                        }

                        int nearestIndex = polygon.findNearestPoint(getCursorPosition(), points);

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
                        break;

                    // výběr barvy vykreslování
                    case KeyEvent.VK_F:
                        chooseLineColor();
                        break;
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

    private void toggleWuAlgorithm() {
        if (useWu) {
            liner = new LineRasterizerXiaolinWu(panel.getRaster());
        } else {
            liner = new FilledLineRasterizer(panel.getRaster());
        }
        liner.setColor(polygonColor);
        useWu = !useWu;

        polygonRasterizer.setLiner(liner);
        drawScene(); //  překreslí polygon
    }

    private void toggleBackground(){
        if(RasterBI.getBackgroundColor().equals(Color.WHITE)){
            RasterBI.setBackgroundColor(Color.BLACK);
        } else {
            RasterBI.setBackgroundColor(Color.WHITE);
        }
        drawScene();
    }

    public void updateRaster(Raster newRaster){
        liner.setRaster(newRaster);
        dashedLiner.setRaster(newRaster);
        polygonRasterizer.setLiner(liner);
    }

    private void chooseLineColor() {
        // otevře dialog pro výběr barvy
        Color selectedColor = JColorChooser.showDialog(panel, "Vyber barvu úsečky", polygonColor);
        if (selectedColor != null) {
            polygonColor = selectedColor;
            liner.setColor(polygonColor);
            drawScene();
        }
    }

    private Point2D getCursorPosition() {
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePosition, panel);
        return new Point2D(mousePosition.getX(), mousePosition.getY());
    }

    public void drawScene() {
        liner.setColor(polygonColor);
        panel.getRaster().clear();
        polygonRasterizer.rasterize(polygon);
        panel.repaint();
    }
}

