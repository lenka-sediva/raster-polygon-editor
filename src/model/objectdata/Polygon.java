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
        return polygon.get(index);
    }

    public int size() {
        return polygon.size();
    }

    // TODO addItemToIndex, removeItem, getFirst, getLast, clear
    // TODO findNearestPoint
}
