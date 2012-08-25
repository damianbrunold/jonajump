package ch.jonajump.items;

import java.awt.Graphics;

public abstract class Item {

    public int x;
    public int y;
    public int width;
    public int height;

    protected Item(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
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
        Item other = (Item) obj;
        if (height != other.height)
            return false;
        if (width != other.width)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    public void update(int x, int y) {
        this.width = Math.max(10, snap(x - this.x));
        this.height = Math.max(10, snap(y - this.y));
    }

    protected static int snap(int i) {
        return (i + 5) / 10 * 10;
    }

    public boolean hit(int x, int y) {
        if (x < this.x || this.x + this.width < x) return false;
        if (y < this.y || this.y + this.height < y) return false;
        return true;
    }

    public boolean hit(int x, int y, int width, int height) {
        return (this.x + this.width >= x) &&
                (this.x <= x + width) &&
                (this.y + this.height >= y) &&
                (this.y <= y + height);
    }

    public void updatePosition() {
        // empty;
    }

    public abstract void render(Graphics g, int start_x, int end_x);

    public int getOffsetX() {
        return 0;
    }

    public int getOffsetY() {
        return 0;
    }
}
