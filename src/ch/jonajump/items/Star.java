package ch.jonajump.items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.jonajump.ResourceLoader;

public class Star extends Item {

	private static BufferedImage image;
	private static int[] offsets;

	public static void init() throws IOException {
		image = ResourceLoader.getImage("items/star");
		offsets = ResourceLoader.getOffsets("items/star");
	}

    public Star(int x, int y) {
        super(x, y, image.getWidth(), image.getHeight());
    }

    public static Star createAt(int x, int y) {
    	return new Star(snap(x), snap(y));
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
        g.drawImage(image, x - start_x, y, null);
    }

    public int getOffsetX() {
        return offsets[0];
    }

    public int getOffsetY() {
        return offsets[1];
    }

}
