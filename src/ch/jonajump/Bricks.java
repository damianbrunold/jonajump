package ch.jonajump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bricks implements Iterable<Brick> {

    private List<Brick> bricks = new ArrayList<Brick>();

    public Bricks() {
        loadBricks();
    }

    public void loadBricks() {
        File file = new File(Bricks.class.getResource("/resources/bricks.txt").getFile().replace("%20", " "));
        if (!file.exists()) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                String line = reader.readLine();
                while (line != null) {
                    bricks.add(parseBrick(line));
                    line = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Brick parseBrick(String s) {
        String[] parts = s.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int width = Integer.parseInt(parts[2]);
        int height = Integer.parseInt(parts[3]);
        int state = Integer.parseInt(parts[4]);
        return new Brick(x, y, width, height, state);
    }

    public void writeBricks() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("src/resources/bricks.txt"));
            try {
                for (Brick brick : bricks) {
                    out.println(brick.toString());
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAt(int x, int y, int state) {
        add(new Brick(x, y, 10, 10, state));
    }

    public void updateLast(int x, int y) {
        last().update(x, y);
    }

    public void add(Brick brick) {
        bricks.add(brick);
    }

    public Brick last() {
        if (bricks.isEmpty()) return null;
        return bricks.get(bricks.size() - 1);
    }

    public Brick pop() {
        return bricks.remove(bricks.size() - 1);
    }

    @Override
    public Iterator<Brick> iterator() {
        return bricks.iterator();
    }

    public boolean isEmpty() {
        return bricks.isEmpty();
    }

    public Brick hit(int x, int y) {
        for (Brick brick : bricks) {
            if (brick.hit(x, y)) return brick;
        }
        return null;
    }

}
