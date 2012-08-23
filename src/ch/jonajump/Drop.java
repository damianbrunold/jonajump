package ch.jonajump;

import java.awt.Graphics;

public class Drop extends Item {

    public Drop(int x, int y, int width, int height) {
        super(x, y, Images.drop.image.getWidth(), Images.drop.image.getHeight());
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ "drop".hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("drop,");
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
        if (x + width < start_x) return;
        if (start_x + end_x < x) return;
        g.drawImage(Images.drop.image, x - start_x, y, null);
    }

    public int getOffsetX() {
        return Images.drop.offset_x;
    }

    public int getOffsetY() {
        return Images.drop.offset_y;
    }
}
