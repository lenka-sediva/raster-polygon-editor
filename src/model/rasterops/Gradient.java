package model.rasterops;

import java.awt.Color;

public class Gradient {

    private final Color startColor;
    private final Color endColor;

    public Gradient(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    /**
     * @param t - hodnota od 0 (start) do 1 (end)
     * @return interpolovaná barva
     */
    public Color getColor(double t) {
        // lineární interpolace každé složky RGB
        int r = (int) Math.round(startColor.getRed()   + t * (endColor.getRed()   - startColor.getRed()));
        int g = (int) Math.round(startColor.getGreen() + t * (endColor.getGreen() - startColor.getGreen()));
        int b = (int) Math.round(startColor.getBlue()  + t * (endColor.getBlue()  - startColor.getBlue()));
        return new Color(r, g, b);
    }
}
