package ch.jonajump.items;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.jonajump.ResourceLoader;

public class Drop extends Item {

	private static BufferedImage image;
	private static int[] offsets;

	public static void init() throws IOException {
		image = ResourceLoader.getImage("items/drop");
		offsets = ResourceLoader.getOffsets("items/drop");
	}

    public Drop(int x, int y) {
        super(x, y, image.getWidth(), image.getHeight());
    }

    public static Drop createAt(int x, int y) {
    	return new Drop(snap(x), snap(y));
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
        g.drawImage(image, x - start_x, y, null);
    }

    public int getOffsetX() {
        return offsets[0];
    }

    public int getOffsetY() {
        return offsets[1];
    }
}
