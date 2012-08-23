package ch.jonajump;

import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {

    public static BufferedImage drop_image;
    public static BufferedImage gold_image;
    public static TexturePaint brick_paint;

    public static void load(int world, int level) throws IOException {
        drop_image = getImage("world" + world + "/level" + level + "/drop");
        gold_image = getImage("world" + world + "/level" + level + "/gold");
        BufferedImage brick_image = getImage("world" + world + "/level" + level + "/brick");
        brick_paint = new TexturePaint(brick_image, new Rectangle(0, 0, brick_image.getWidth(), brick_image.getHeight()));
    }

    private static BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(new File(Images.class.getResource("/resources/" + name + ".png").getFile().replace("%20", " ")));
    }

}
