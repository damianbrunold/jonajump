package ch.jonajump;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Brick extends Item {

    public Brick(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ "brick".hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("brick,");
        result.append(x).append(",");
        result.append(y).append(",");
        result.append(width).append(",");
        result.append(height);
        return result.toString();
    }

    public void render(Graphics g, int start_x, int end_x) {
        if (x + width < start_x) return;
        if (start_x + end_x < x) return;
        ((Graphics2D) g).setPaint(Images.brick_paint);
        g.fillRect(x - start_x, y, width, height);
    }

}
