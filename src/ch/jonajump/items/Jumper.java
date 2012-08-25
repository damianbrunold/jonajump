package ch.jonajump.items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.jonajump.ResourceLoader;

public class Jumper extends Item {

    private static final int MAX_JUMP_Y = 80;
    private static final int JUMP_WAVELENGTH = 60;

    private static BufferedImage image;
    private static int[] offsets;

    private int jump_phase = 0;
    private int y0;

    public static void init() throws IOException {
        image = ResourceLoader.getImage("items/jumper");
        offsets = ResourceLoader.getOffsets("items/jumper");
    }

    public Jumper(int x, int y) {
        super(x, y, image.getWidth(), image.getHeight());
        y0 = y;
    }

    public static Jumper createAt(int x, int y) {
        return new Jumper(snap(x), snap(y));
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ "jumper".hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("jumper,");
        result.append(x).append(",");
        result.append(y).append(",");
        result.append(width).append(",");
        result.append(height);
        return result.toString();
    }

    public void update(int x, int y) {
        // nothing to update
    }

    public void render(Graphics g, int start_x, int end_x) {
        if (x + width < start_x)
            return;
        if (start_x + end_x < x)
            return;
        g.drawImage(image, x - start_x, y, null);
    }

    public int getOffsetX() {
        return offsets[0];
    }

    public int getOffsetY() {
        return offsets[1];
    }

    public void updatePosition() {
        jump_phase++;
        y = y0 - Math.abs((int) (MAX_JUMP_Y * Math.sin(jump_phase * 2 * Math.PI / JUMP_WAVELENGTH)));
    }

}
