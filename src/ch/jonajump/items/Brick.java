package ch.jonajump.items;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.jonajump.ResourceLoader;

public class Brick extends Item {

	private static BufferedImage image;

	public static void init(int world, int level) throws IOException {
		image = ResourceLoader.getImage("world" + world + "/level" + level + "/brick");
	}

    public Brick(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public static Brick createAt(int x, int y) {
    	return new Brick(snap(x), snap(y), 10, 10);
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
        TexturePaint paint = new TexturePaint(image, new Rectangle(image.getWidth() - start_x % image.getWidth(), 0, image.getWidth(), image.getHeight()));
        ((Graphics2D) g).setPaint(paint);
        g.fillRect(x - start_x, y, width, height);
    }

}
