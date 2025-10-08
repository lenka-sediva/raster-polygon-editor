package model.rasterdata;

import java.awt.*;

public interface Raster {

    /**
     * Metoda pre vycistenie rastra
     */
    void clear();

    /**
     * Metoda pre preklesenie platna
     * @param g
     */
    void repaint(Graphics g);

    /**
     * Vrati pocet stlpcov v rastri
     * @return pocet stlpcov
     */
    int getWidth();

    /**
     * Vrati pocet riadkov v rastri
     * @return pocet riakdov
     */
    int getHeight();

    /**
     * Vrati hodnotu (farbu), ktora je ulozena na denj adrese
     * @param x adresa stlpca daneho pixelu
     * @param y adresa riadka daneho pixelu
     * @return ulozena hodnota (farba)
     */
    int getPixel(int x, int y);

    /**
     * Nastavi farbu pixelu na zadanej adrese
     * @param x adresa stlpca daneho pixelu
     * @param y adresa riadka daneho pixelu
     * @param color nova farba
     */
    void setPixel(int x, int y, int color);
}
