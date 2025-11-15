package controller;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterdata.Raster;
import model.rasterdata.RasterBI;
import model.objectdata.Rectangle;
import model.rasterops.clip.SutherlandHodgman;
import model.rasterops.filler.ScanLine;
import model.rasterops.filler.SeedFill;
import model.rasterops.filler.PatternFill;
import model.rasterops.rasterizer.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Controller2D implements Controller {

    private final Panel panel;
    private final LineRasterizer liner;
    private final PolygonRasterizer polygonRasterizer;
    private boolean isRasterizingWithShift = false;

    private final List<Polygon> polygons = new ArrayList<>();
    private final List<Rectangle> rectangles = new ArrayList<>();
    private Polygon polygon = new Polygon();
    private Polygon pentagon; // původní polygon, podle kterého se ořezává
    private final List<Line> lines; // ukládání úseček
    private final Deque<Line> undoStack = new ArrayDeque<>();
    private final Deque<Line> redoStack = new ArrayDeque<>();
    private Color polygonColor = Color.BLUE;
    private final DashedLineRasterizer dashedLiner;

    private int draggedPointIndex = -1; // žádný bod není vybraný pro tažení
    private boolean isDragging = false;

    // vyplňování
    private SeedFill seedFill;
    private ScanLine scanLine;
    private int fillPatternIndex = 0;
    private static final int totalPatterns = 4;
    private boolean fillMode = false;
    private PatternFill pattern;

    // obdélník
    private Point2D start, end;
    private Rectangle rectangle;

    // 2 módy
    public enum DrawMode { POLYGON, RECTANGLE }
    private DrawMode currentMode = DrawMode.POLYGON;

    public enum FillModeType { ON, OFF }
    private FillModeType currentFillMode = FillModeType.OFF;

    private Polygon selectedPolygon = null;

    public Controller2D(Panel panel) {
        this.panel = panel;

        liner = new FilledLineRasterizer(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(liner);
        liner.setColor(polygonColor);

        this.lines = new ArrayList<>();

        this.dashedLiner = new DashedLineRasterizer(panel.getRaster());

        seedFill = new SeedFill(panel.getRaster());
        scanLine = new ScanLine(panel.getRaster(), polygon, polygonRasterizer);

        // výchozí stavy
        panel.setFillModeText("Fill mode: OFF [L]");
        panel.setDrawModeText("Mode: POLYGON [P]");
        panel.setDrawModeShortcut("[E] pro obdélník");
        panel.setPatternText("Pattern: Vertical Stripes [V]");

        initObjects();
        initListeners(panel);
    }

    @Override
    public void initObjects() {
        // vykreslení pětiúhelníku, podle kterého se ořezává, na začátku
        pentagon = new Polygon();

        pentagon.addItem(new Point2D(250, 100));
        pentagon.addItem(new Point2D(400, 300));
        pentagon.addItem(new Point2D(330, 500));
        pentagon.addItem(new Point2D(170, 500));
        pentagon.addItem(new Point2D(100, 300));

        polygons.add(pentagon);

        polygon = new Polygon();
        polygons.add(polygon);
        drawScene();
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

                switch (currentMode) {
                    case POLYGON -> handlePolygonPressed(newPoint, e);
                    case RECTANGLE -> handleRectanglePressed(newPoint);
                }

                pattern = PatternFill.verticalStripes(5, polygonColor, Color.WHITE);

                seedFill.setSeed(
                        newPoint.getX(),
                        newPoint.getY(),
                        Color.WHITE.getRGB(),
                        0, // 0, pokud chceme vyplňovat podle pozadí, polygonColor.getRGB() podle hranice
                        pattern, // null (a zároveň hranice 0), pokud chceme vybarvení jednolitě
                        false
                );

                if(!fillMode && currentMode == DrawMode.POLYGON) {
                    // levé tlačítko - přidání prvního bodu polygonu (nutné, jinak nejde drag prvního bodu)
                    if (polygon.size() == 0) {
                        polygon.addItem(newPoint);
                        drawScene();
                    }
                }

                if (fillMode) {
                    // vyplňování od semínka (kliknutí)
                    if (Line.isPointInsidePolygon(newPoint, polygon)) {
                        seedFill.fill();
                        panel.repaint();
                    }

                    for (Rectangle r : rectangles) {
                        if (Line.isPointInsidePolygon(newPoint, r)) {
                            seedFill.fill();
                            panel.repaint();
                            break;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point2D newPoint = new Point2D(e.getX(), e.getY());

                boolean insideAnyRectangle = false;
                for (Rectangle r : rectangles) {
                    if (Line.isPointInsidePolygon(newPoint, r)) {
                        insideAnyRectangle = true;
                        break;
                    }
                }

                if(fillMode && !Line.isPointInsidePolygon(newPoint, polygon) && !insideAnyRectangle) {
                    drawScene(); // překreslení scény, aby zmizela pružná čára
                    // zobrazí upozornění uživateli
                    JOptionPane.showMessageDialog(panel,
                            "Nelze tvořit nové útvary v režimu vyplňování.\nStisknutím [L] režim vypnete.",
                            "Režim vyplňování",
                            JOptionPane.WARNING_MESSAGE);
                    start = null; // reset obdélníku
                    return; // nedovolí pokračovat v kreslení
                }

                switch (currentMode) {
                    case POLYGON -> handlePolygonReleased(e);
                    case RECTANGLE -> handleRectangleReleased(e);
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawScene();

                // změna pozice vybraného vrcholu polygonu
                if(isDragging && draggedPointIndex != -1) {
                    Point2D dragged = selectedPolygon.getItem(draggedPointIndex);
                    dragged.setX(e.getX());
                    dragged.setY(e.getY());
                    drawScene(); // po posunu vrchol automaticky přepočítává ořezaný polygon a vyplní ho
                    return;
                }

                dashedLiner.setGradient(polygonColor, Color.RED); // gradient vždy od vybrané barvy do červené

                // vykreslení pružné dashed čáry
                if(polygon.size() > 0) {
                    Point2D newPoint = new Point2D(e.getX(), e.getY());
                    Line tempLine = new Line(polygon.getLast(), newPoint);

                    if (isRasterizingWithShift) {
                        dashedLiner.rasterize(new Line(polygon.getFirst(), polygon.findNearestPointToAxisCandidates(tempLine)));
                        dashedLiner.rasterize(new Line(polygon.getLast(), polygon.findNearestPointToAxisCandidates(tempLine)));
                    } else {
                        dashedLiner.rasterize(new Line(polygon.getFirst(), newPoint));
                        dashedLiner.rasterize(new Line(polygon.getLast(), newPoint));
                    }
                }

                // obdélník - přidání 2. bodu (konce základny) pomocí dragu myši
                if (currentMode == DrawMode.RECTANGLE && rectangle != null && rectangle.size() == 1) {
                    drawScene();
                    end = new Point2D(e.getX(), e.getY());
                    Line base = new Line(start, end);
                    dashedLiner.rasterize(base);
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
                        rectangles.clear();
                        drawScene();
                        break;

                    // vykreslování úseček podle 3 typů směrů
                    case KeyEvent.VK_SHIFT:
                        isRasterizingWithShift = true;
                        break;

                    // přidání nového vrcholu polygonu do nejbližší hrany
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
                        int nearestIndex = polygon.findNearestPoint(getCursorPosition(), polygon.toArray());
                        if (nearestIndex != -1) {
                            polygon.removeItem(nearestIndex);
                            drawScene();
                        }
                        break;

                    // výběr barvy vykreslování
                    case KeyEvent.VK_F:
                        // otevře dialog pro výběr barvy
                        Color selectedColor = JColorChooser.showDialog(panel, "Vyber barvu úsečky", polygonColor);
                        if (selectedColor != null) {
                            polygonColor = selectedColor;
                            liner.setColor(polygonColor);
                            drawScene();
                        }
                        break;

                    // přepne na režim vyplňování pro seedfill
                    case KeyEvent.VK_L:
                        fillMode = !fillMode;
                        currentFillMode = (currentFillMode == FillModeType.ON) ? FillModeType.OFF : FillModeType.ON;
                        panel.setFillModeText("Fill mode: " + currentFillMode.name() + " [L]");
                        break;

                    // mód polygonu
                    case KeyEvent.VK_P:
                        currentMode = DrawMode.POLYGON;
                        panel.setDrawModeText("Mode: " + currentMode.name() + " [P]");
                        panel.setDrawModeShortcut("[E] pro obdélník");
                        break;

                    // mód obdélníku
                    case KeyEvent.VK_E:
                        currentMode = DrawMode.RECTANGLE;
                        panel.setDrawModeText("Mode: " + currentMode.name() + " [E]");
                        panel.setDrawModeShortcut("[P] pro polygon");
                        break;

                    // přepínání vzorů po ořezu pro scanline
                    case KeyEvent.VK_V:
                        fillPatternIndex = (fillPatternIndex + 1) % totalPatterns;
                        String patternName = switch (fillPatternIndex) {
                            case 1 -> "Checkerboard";
                            case 2 -> "Horizontal Stripes";
                            case 3 -> "Dots";
                            default -> "Vertical Stripes";
                        };
                        panel.setPatternText("Pattern: " + patternName + " [V]");
                        drawScene();
                        break;

                    // odebrání poslední úsečky polygonu
                    case KeyEvent.VK_Z:
                        if (!undoStack.isEmpty()) {
                            Line last = undoStack.removeLast(); // vezmi poslední provedenou akci
                            redoStack.addLast(last); // přesun do redo
                            // odeber z lines (poslední výskyt)
                            for (int i = lines.size() - 1; i >= 0; i--) {
                                if (lines.get(i) == last) {
                                    lines.remove(i);
                                    break;
                                }
                            }
                            // odeber bod z polygonu (pokud tam je)
                            if (polygon.size() > 0) {
                                polygon.removeItem(polygon.size() - 1);
                            }
                            drawScene();
                        }
                        break;

                    // vrácení poslední odebrané úsečky
                    case KeyEvent.VK_Y:
                        if (!redoStack.isEmpty()) {
                            Line redoLine = redoStack.removeLast(); // vezmi poslední odebranou akci
                            undoStack.addLast(redoLine); // vrať do historie
                            lines.add(redoLine); // znovu do zobrazení
                            polygon.addItem(redoLine.getEnd());
                            drawScene();
                        }
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

    public void updateRaster(Raster newRaster){
        liner.setRaster(newRaster);
        dashedLiner.setRaster(newRaster);
        polygonRasterizer.setLiner(liner);
        seedFill = new SeedFill(newRaster);  // aktualizace SeedFill s novým rastrem
        scanLine = new ScanLine(newRaster, polygon, polygonRasterizer);
    }

    private Point2D getCursorPosition() {
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePosition, panel);
        return new Point2D(mousePosition.getX(), mousePosition.getY());
    }

    private void handlePolygonPressed(Point2D newPoint, MouseEvent e) {
        // pravé tlačítko - najde nejbližší bod pro tažení (změna vrcholu)
        if (e.getButton() == MouseEvent.BUTTON3) {
            // porovnání vzdálenosti kurzoru k nejbližším bodům polygonů
            int nearestIndexPentagon = pentagon.findNearestPoint(newPoint, pentagon.toArray());
            int nearestIndexPolygon = polygon.findNearestPoint(newPoint, polygon.toArray());

            double distPentagon = newPoint.distanceTo(pentagon.getItem(nearestIndexPentagon));
            double distPolygon = newPoint.distanceTo(polygon.getItem(nearestIndexPolygon));

            if (distPentagon < distPolygon) {
                selectedPolygon = pentagon;
                draggedPointIndex = nearestIndexPentagon;
            } else {
                selectedPolygon = polygon;
                draggedPointIndex = nearestIndexPolygon;
            }
            isDragging = true;
        }
    }

    private void handlePolygonReleased(MouseEvent e){
        if (isDragging) { // konec dragování
            isDragging = false;
            draggedPointIndex = -1;
            selectedPolygon = null;
            drawScene();
            return;
        }

        // v režimu vyplňování se po kliknutí nepřidává nový bod
        if(fillMode) {
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
        undoStack.addLast(tempLine);
        redoStack.clear();
        drawScene();
    }

    private void handleRectanglePressed(Point2D newPoint) {
        if (start == null) {
            // 1. klik = začátek základny
            start = newPoint;
            rectangle = new Rectangle();
            rectangle.addItem(start);
        } else if (rectangle.size() == 2) {
            // 3. klik = výška a vytvoření rectangle
            rectangle = new Rectangle(start, end, newPoint);
            rectangles.add(rectangle);
            start = null;
            end = null;
            rectangle = null;
            drawScene();
        }
    }

    private void handleRectangleReleased(MouseEvent e) {
        if (rectangle == null) return;

        if (rectangle.size() == 1 && isClickWithoutDrag(start, new Point2D(e.getX(), e.getY())) && !fillMode) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Obdélník nelze vytvořit pouze kliknutím.\n" +
                            "Chyťte myší bod a táhněte pro vytvoření základny.",
                    "Chyba kreslení obdélníku",
                    JOptionPane.WARNING_MESSAGE
            );
            start = null;
        }

        if (rectangle.size() == 1 && end != null) {
            // 2. bod základny
            rectangle.addItem(end);
            rectangles.add(rectangle);
            Line base = new Line(start, end);
            lines.add(base);
            drawScene();
        }
    }

    private boolean isClickWithoutDrag(Point2D start, Point2D end) {
        if (start == null || end == null) return false;
        double dx = Math.abs(start.getX() - end.getX());
        double dy = Math.abs(start.getY() - end.getY());
        return dx < 3 && dy < 3; // tolerance 3 px
    }

    // přepočítá clipped polygon (ořezaný pentagonem) a provede scanline vyplnění
    private void updateClippedAndFill(Polygon shape) {
        // oříznutí polygonu nebo obdélníku (shape) podle původního pentagonu
        SutherlandHodgman clipper = new SutherlandHodgman();
        Polygon clipped = clipper.clip(shape, pentagon);

        scanLine = new ScanLine(panel.getRaster(), clipped, polygonRasterizer);
        PatternFill pattern = switch (fillPatternIndex) {
            case 1 -> PatternFill.checkerboard(10, polygonColor, RasterBI.getBackgroundColor());
            case 2 -> PatternFill.horizontalStripes(5, polygonColor, RasterBI.getBackgroundColor());
            case 3 -> PatternFill.dots(3, polygonColor, RasterBI.getBackgroundColor());
            default -> PatternFill.verticalStripes(5, polygonColor, RasterBI.getBackgroundColor());
        };
        scanLine.setPattern(pattern);
        scanLine.fill();
    }

    public void drawScene() {
        panel.getRaster().clear(); // aby se stále dokola nevykreslovaly pružné čáry, ale jen 1

        // vykreslí všechny uložené polygony
        for (Polygon p : polygons) {
            polygonRasterizer.rasterize(p); // vykreslení hranice
        }

        // vykreslí všechny uložené obdélníky
        for (Rectangle r : rectangles) {
            polygonRasterizer.rasterize(r);
        }

        // ořízne polygon podle pentagonu a vyplní vzniklý tvar
        updateClippedAndFill(polygon);

        for (Rectangle r : rectangles) {
            updateClippedAndFill(r);
        }

        panel.repaint();
    }
}
