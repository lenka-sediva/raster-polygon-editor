package model.objectdata;

import java.util.ArrayList;

public class Polygon {

    private ArrayList<Point2D> polygon;

    public Polygon() {
        this.polygon = new ArrayList<>();
    }

    public void addItem(Point2D p) {
        polygon.add(p);
    }

    public Point2D getItem(int index) {
        if (index >= 0 && index < polygon.size()) {
            return polygon.get(index);
        }
        return null;
    }

    public int size() {
        return polygon.size();
    }

    public void addItemToIndex(int index, Point2D p) {
        if (index >= 0 && index < polygon.size()) {
            polygon.add(index, p);
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < polygon.size()) {
            polygon.remove(index);
        }
    }

    public Point2D getFirst() {
        if (!polygon.isEmpty()) {
            return polygon.get(0);
        }
        return null;
    }

    public Point2D getLast() {
        if (!polygon.isEmpty()) {
            return polygon.get(polygon.size() -1);
        }
        return null;
    }

    public void clear() {
        polygon.clear();
    }

    public int findNearestPoint(Point2D point) {
        if (polygon.isEmpty()) {
            return -1;
        }

        double minDistance = Double.MAX_VALUE;
        int nearestIndex = -1;

        for (int i = 0; i < polygon.size(); i++) {
            Point2D p = polygon.get(i);
            double dx = p.getX() - point.getX();
            double dy = p.getY() - point.getY();
            double distance = Math.sqrt(dx*dx + dy*dy);

            if(distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }
}
