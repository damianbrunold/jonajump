package ch.jonajump;

public class Brick {

    public static final int SOLID_STATE = 1;
    public static final int DEADLY_STATE = 2;

    public int x;
    public int y;
    public int width;
    public int height;
    public int state;

    public Brick(int x, int y, int width, int height, int state) {
        this.x = snap(x);
        this.y = snap(y);
        this.width = Math.max(10, snap(width));
        this.height = Math.max(10, snap(height));
        this.state = state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + state;
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
        if (state != other.state)
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
        result.append(state);
        return result.toString();
    }

    public void update(int x, int y) {
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
}
