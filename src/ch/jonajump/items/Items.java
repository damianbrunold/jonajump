package ch.jonajump.items;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.jonajump.ResourceLoader;

public class Items implements Iterable<Item> {

    private List<Item> items = new ArrayList<Item>();

    public Items(int world, int level) throws IOException {
        loadItems(world, level);
    }

    public void loadItems(int world, int level) throws IOException {
        File file = ResourceLoader.getFile("world" + world + "/level" + level + "/items.txt");
        if (!file.exists()) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                String line = reader.readLine();
                while (line != null) {
                    items.add(parseItem(line));
                    line = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Item parseItem(String s) {
        String[] parts = s.split(",");
        String type = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int width = Integer.parseInt(parts[3]);
        int height = Integer.parseInt(parts[4]);
        if (type.equals("brick")) {
            return new Brick(x, y, width, height);
        } else if (type.equals("drop")) {
            return new Drop(x, y);
        } else if (type.equals("gold")) {
            return new Gold(x, y);
        } else if (type.equals("star")) {
            return new Star(x, y);
        } else if (type.equals("jumper")) {
            return new Jumper(x, y);
        }
        throw new RuntimeException("unknown item type " + type);
    }

    public void writeItems(int world, int level) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("src/resources/world" + world + "/level" + level + "/items.txt"));
            try {
                for (Item item : items) {
                    out.println(item.toString());
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAt(int x, int y, int type) {
        Item item;
        if (type == 1) {
            item = Brick.createAt(x, y);
        } else if (type == 2) {
            item = Drop.createAt(x, y);
        } else if (type == 3) {
            item = Gold.createAt(x, y);
        } else if (type == 4) {
            item = Star.createAt(x, y);
        } else if (type == 5) {
            item = Jumper.createAt(x, y);
        } else {
            throw new RuntimeException("unknown type " + type);
        }
        item.x -= item.getOffsetX();
        item.y -= item.getOffsetY();
        add(item);
    }

    public void updateLast(int x, int y) {
        last().update(x, y);
    }

    public void add(Item item) {
        items.add(item);
    }

    public void remove(Item item) {
        items.remove(item);
    }

    public Item last() {
        if (items.isEmpty()) return null;
        return items.get(items.size() - 1);
    }

    public Item pop() {
        return items.remove(items.size() - 1);
    }

    public Iterator<Item> iterator() {
        return items.iterator();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Item hit(int x, int y) {
        for (Item item : items) {
            if (item.hit(x, y)) return item;
        }
        return null;
    }

    public List<Item> hit(int x, int y, int width, int height) {
        List<Item> result = new ArrayList<Item>();
        for (Item item : items) {
            if (item.hit(x, y, width, height)) result.add(item);
        }
        return result;
    }

    public void updatePositions() {
        for (Item item : items) {
            item.updatePosition();
        }
    }

}
