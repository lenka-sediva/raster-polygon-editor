package model.rasterops.filler;

import java.awt.Color;

public interface PatternFill {
    // vrátí barvu pro daný bod (x, y) podle vzoru
    Color getColor(int x, int y);

    // šachovnicový vzor
    static PatternFill checkerboard(int cellSize, Color color1, Color color2) {
        return (x, y) -> {
            int cx = (x / cellSize) % 2;
            int cy = (y / cellSize) % 2;
            return (cx + cy) % 2 == 0 ? color1 : color2;
        };
    }

    // horizontální pruhy
    static PatternFill horizontalStripes(int height, Color color1, Color color2) {
        return (x, y) -> (y / height) % 2 == 0 ? color1 : color2;
    }

    // vertikální pruhy
    static PatternFill verticalStripes(int width, Color color1, Color color2) {
        return (x, y) -> (x / width) % 2 == 0 ? color1 : color2;
    }

    // tečkovaný vzor
    static PatternFill dots(int spacing, Color color1, Color color2) {
        return (x, y) -> (x % spacing == 0 && y % spacing == 0) ? color1 : color2;
    }
}
