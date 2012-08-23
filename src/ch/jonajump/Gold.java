package ch.jonajump;

import java.awt.Graphics;

public class Gold extends Item {

    public Gold(int x, int y, int width, int height) {
        super(x, y, Images.gold.image.getWidth(), Images.gold.image.getHeight());
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ "gold".hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("gold,");
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
        g.drawImage(Images.gold.image, x - start_x, y, null);
    }

    public int getOffsetX() {
        return Images.gold.offset_x;
    }

    public int getOffsetY() {
        return Images.gold.offset_y;
    }

}
