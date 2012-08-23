package ch.jonajump;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Brick {

    public static final int BRICK = 1;
    public static final int DROP = 2;
    public static final int GOLD = 3;

    public int x;
    public int y;
    public int width;
    public int height;
    public int type;

    public Brick(int x, int y, int width, int height, int type) {
        this.x = snap(x);
        this.y = snap(y);
        this.type = type;
        if (type == BRICK) {
            this.width = Math.max(10, snap(width));
            this.height = Math.max(10, snap(height));
        } else if (type == DROP) {
            this.width = Images.drop_image.getWidth();
            this.height = Images.drop_image.getHeight();
        } else if (type == GOLD) {
            this.width = Images.gold_image.getWidth();
            this.height = Images.gold_image.getHeight();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + type;
        result = prime * result + width;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Brick other = (Brick) obj;
        if (height != other.height)
            return false;
        if (type != other.type)
            return false;
        if (width != other.width)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(x).append(",");
        result.append(y).append(",");
        result.append(width).append(",");
        result.append(height).append(",");
        result.append(type);
        return result.toString();
    }

    public void update(int x, int y) {
        if (type != BRICK) return;
        this.width = Math.max(10, snap(x - this.x));
        this.height = Math.max(10, snap(y - this.y));
    }

    private int snap(int i) {
        return (i + 5) / 10 * 10;
    }

    public boolean hit(int x, int y) {
        if (x < this.x || this.x + this.width < x) return false;
        if (y < this.y || this.y + this.height < y) return false;
        return true;
    }

    public void render(Graphics g, int start_x, int end_x) {
        if (x + width < start_x) return;
        if (start_x + end_x < x) return;
        if (type == BRICK) {
            ((Graphics2D) g).setPaint(Images.brick_paint);
            g.fillRect(x - start_x, y, width, height);
        } else if (type == DROP) {
            g.drawImage(Images.drop_image, x - start_x, y, null);
        } else if (type == GOLD) {
            g.drawImage(Images.gold_image, x - start_x, y, null);
        }
    }

}
