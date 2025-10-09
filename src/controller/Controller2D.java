package controller;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerGraphics;
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
    private Color color;
    private boolean dragging = false;
    private int startX, startY;

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

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // první bod
                startX = e.getX();
                startY = e.getY();
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // potvrzení druhého bodu
                if(dragging) {
                    dragging = false;
                    int endX = e.getX();
                    int endY = e.getY();

                    // přidání úsečky do Polygonu (seznam)
                    polygon.addItem(new Point2D(endX,endY));

                    // vykreslení potvrzené čáry
                    liner.rasterize(startX, startY, endX, endY, color);
                    panel.repaint();
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(!dragging) return;

                int currentX = e.getX();
                int currentY = e.getY();

                

                // vykreslení pružné čáry
                liner.rasterize(startX, startY, currentX, currentY, color);
                panel.repaint();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            // mazání
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_C) {
                    panel.clear();
                }
            }
        });
    }
}
