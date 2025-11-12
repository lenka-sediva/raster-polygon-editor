package model.objectdata;

public class Rectangle extends Polygon {
    public Rectangle(Point2D start, Point2D end, Point2D height) {
        super();
        buildVertices(start, end, height);
    }

    public Rectangle(){
        super();
    }

    /**
     * Dokončí obdélník podle základny a bodu určujícího výšku
     * @param start začátek základny
     * @param end konec základny
     * @param height bod určující výšku obdélníku
     */
    public void buildVertices(Point2D start, Point2D end, Point2D height) {
        // základna
        double dx = start.x - end.x;
        double dy = start.y - end.y;

        // délka základny
        double length = Math.sqrt(dx * dx + dy * dy);

        // normálový (kolmý) vektor základny
        double nx = -dy / length;
        double ny = dx / length;

        // výška = projekce vzdálenosti třetího bodu na normálu
        double vx = height.getX() - start.getX();
        double vy = height.getY() - start.getY();
        double h = vx * nx + vy * ny;

        // 4 vrcholy obdélníku
        Point2D p1 = start;
        Point2D p2 = end;
        Point2D p3 = new Point2D((int) (end.getX() + nx * h), (int) (end.getY() + ny * h));
        Point2D p4 = new Point2D((int) (start.getX() + nx * h), (int) (start.getY() + ny * h));

        addItem(p1);
        addItem(p2);
        addItem(p3);
        addItem(p4);
    }

    public void setTemporaryEnd(Point2D end) {
        if (size() < 2) {
            if (size() == 0) return;
            addItem(end);
        } else {
            removeItem(1);
            addItemToIndex(1, end);
        }
    }
}
