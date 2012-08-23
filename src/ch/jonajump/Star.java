package ch.jonajump;

import java.awt.Graphics;

public class Star extends Item {

    public Star(int x, int y, int width, int height) {
        super(x, y, Images.star.image.getWidth(), Images.star.image.getHeight());
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ "star".hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("star,");
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
        g.drawImage(Images.star.image, x - start_x, y, null);
    }

    public int getOffsetX() {
        return Images.star.offset_x;
    }

    public int getOffsetY() {
        return Images.star.offset_y;
    }

}
