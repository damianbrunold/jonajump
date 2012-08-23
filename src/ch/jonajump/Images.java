package ch.jonajump;

import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Images {

    public static class Item {
        public BufferedImage image;
        public int offset_x;
        public int offset_y;
    }

    public static Item drop;
    public static Item gold;
    public static Item star;

    public static TexturePaint brick_paint;

    public static void load(int world, int level) throws IOException {
        drop = loadItem(world, level, "drop");
        gold = loadItem(world, level, "gold");
        star = loadItem(world, level, "star");
        brick_paint = loadTexture(world, level, "brick");
    }

    private static Item loadItem(int world, int level, String name) throws IOException {
        Item item = new Item();
        item.image = ResourceLoader.getImage(getResourceName(world, level, name));
        BufferedReader reader = new BufferedReader(new FileReader(ResourceLoader.getFile(getResourceName(world, level, name + ".txt"))));
        try {
            item.offset_x = Integer.parseInt(reader.readLine());
            item.offset_y = Integer.parseInt(reader.readLine());
        } finally {
            reader.close();
        }
        return item;
    }

    private static TexturePaint loadTexture(int world, int level, String name) throws IOException {
        BufferedImage image = ResourceLoader.getImage(getResourceName(world, level, name));
        return new TexturePaint(image, new Rectangle(0, 0, image.getWidth(), image.getHeight()));
    }

    private static String getResourceName(int world, int level, String name) {
        return "world" + world + "/level" + level + "/" + name;
    }
}
