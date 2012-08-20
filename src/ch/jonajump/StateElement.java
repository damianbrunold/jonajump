package ch.jonajump;

public class StateElement {

    public int x;
    public int y;
    public int width;
    public int height;
    public int state;

    public StateElement(int x, int y, int width, int height, int state) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
        StateElement other = (StateElement) obj;
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
}
