package ch.jonajump;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Player {

    private static final int RUN_VELOCITY = 10;
    private static final double RUN_ACCEL = 1.0;

    private static final double AIR_VELOCITY_DECAY = 0.92;
    private static final double GROUND_VELOCITY_DECAY = 0.5;

    private static final int JUMP_ACCEL_STANDING = 11;
    private static final int JUMP_ACCEL_RUNNING = 14;
    private static final int JUMP_VELOCITY = JUMP_ACCEL_RUNNING;
    private static final int FALL_VELOCITY = -JUMP_VELOCITY;

    private static final double GRAVITY = -1.0;

    private boolean dead = false;

    public int world_width;
    public int world_height;

    public int width;
    public int height;

    public int x = 0;
    public int y = 0;

    private double velocity_x = 0;
    private double velocity_y = 0;
    private double accel_x = 0;
    private double accel_y = 0;

    public boolean looking_right = true;
    public boolean running = false;
    public boolean jumping = false;
    public boolean down = false;

    private BufferedImage[] images = new BufferedImage[6];
    private BufferedImage image;

    private int character;

    private Items items;
    private Treasure treasure;

    public Player(int character, Items items, Treasure treasure, int world_width, int world_height) {
    	this.character = character;
    	this.items = items;
    	this.treasure = treasure;
    	this.world_width = world_width;
    	this.world_height = world_height;
        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
        width = image.getWidth();
        height = image.getHeight();
    }

    private void loadImages() throws IOException {
        images[0] = ResourceLoader.getImage("player" + character + "/standing_left");
        images[1] = ResourceLoader.getImage("player" + character + "/standing_right");
        images[2] = ResourceLoader.getImage("player" + character + "/jumping_left");
        images[3] = ResourceLoader.getImage("player" + character + "/jumping_right");
        images[4] = ResourceLoader.getImage("player" + character + "/running_left");
        images[5] = ResourceLoader.getImage("player" + character + "/running_right");
        image = images[1];
    }

    public synchronized void startRunningRight() {
        looking_right = true;
        running = true;
        accel_x = RUN_ACCEL;
    }

    public synchronized void startRunningLeft() {
        looking_right = false;
        running = true;
        accel_x = RUN_ACCEL;
    }

    public synchronized void stopRunning() {
        running = false;
        accel_x = 0;
    }

    public synchronized void setDown(boolean down) {
    	this.down = down;
    }

    public synchronized void jump() {
        jumping = true;
        Item hit = items.hit(x + width / 2, y + 2);
        if (hit != null && hit instanceof Brick) {
            velocity_y = running ? JUMP_ACCEL_RUNNING : JUMP_ACCEL_STANDING;
            if (down) {
                velocity_y *= -1;
                y += 21; // FIXME
            }
        }
    }

    public synchronized void move() {
    	calcVelocityX();
    	calcVelocityY();
    	updatePosition();
    	collectStuff();
    }

    private void calcVelocityX() {
        if (accel_x != 0) {
            velocity_x = Math.min(RUN_VELOCITY, velocity_x + accel_x);
        } else if (items.hit(x + width / 2, y + 1) == null) {
            velocity_x *= AIR_VELOCITY_DECAY;
            if (velocity_x < 1) velocity_x = 0;
        } else {
            velocity_x *= GROUND_VELOCITY_DECAY;
            if (velocity_x < 1) velocity_x = 0;
        }
    }

    private void calcVelocityY() {
        velocity_y += accel_y + GRAVITY;
        velocity_y = Math.min(JUMP_VELOCITY, Math.max(FALL_VELOCITY, velocity_y));
    }

    private void updatePosition() {
    	int new_x = nextPositionX();
    	int new_y = nextPositionY();
    	List<Item> hit_items = items.hit(new_x, new_y - height, width, height);
    	x = hitCheckX(new_x, new_y, hit_items);
        y = hitCheckY(new_x, new_y, hit_items);
        setImage();
    }

    private int nextPositionX() {
        if (velocity_x == 0) return x;
        if (looking_right) {
            return Math.min((int) (x + velocity_x), world_width);
        } else {
            return Math.max((int) (x - velocity_x), 0);
        }
    }

    private int nextPositionY() {
        if (velocity_y != 0) {
            return (int) (y - velocity_y);
        }
        return y;
    }

    private int hitCheckX(int new_x, int new_y, List<Item> hit_items) {
        for (Item item : hit_items) {
            if (item != null && item instanceof Brick) {
                Brick brick = (Brick) item;
                if (isBrickToRight(brick) && looking_right) {
                    new_x = brick.x - width - 1;
                    looking_right = false;
                    accel_x = 0;
                    velocity_x /= 4;
                    break;
                } else if (isBrickToLeft(brick) && !looking_right) {
                    new_x = brick.x + brick.width + 1;
                    looking_right = true;
                    accel_x = 0;
                    velocity_x /= 4;
                    break;
                }
            }
        }
    	return new_x;
    }

    private int hitCheckY(int new_x, int new_y, List<Item> hit_items) {
        for (Item item : hit_items) {
            if (item != null && item instanceof Brick) {
                Brick brick = (Brick) item;
                if (isBrickBelow(brick)) {
                    new_y = item.y - 1;
                    velocity_y = 0;
                    accel_y = 0;
                    if ((y - new_y) > 1) velocity_x = 0;
                    break;
                } else if (isBrickAbove(brick)) {
                    new_y = item.y + item.height + height + 1;
                    velocity_y = 0;
                    accel_y = 0;
                    break;
                }
            }
        }
        if (new_y > world_height) dead = true;
        return new_y;
    }

    private boolean isBrickToLeft(Brick brick) {
        return brick.x + brick.width < x && brick.y <= y && brick.y + brick.height >= y - height;
    }

    private boolean isBrickToRight(Brick brick) {
        return x + width < brick.x && brick.y <= y && brick.y + brick.height >= y - height;
    }

    private boolean isBrickAbove(Brick brick) {
        return brick.y + brick.height < y - height && brick.x <= x + width && brick.x + brick.width >= x;
    }

    private boolean isBrickBelow(Brick brick) {
        return y < brick.y && brick.x <= x + width && brick.x + brick.width >= x;
    }

    private void collectStuff() {
        List<Item> hit_items = items.hit(x, y - height, width, height - 1);
        if (!hit_items.isEmpty()) {
            for (Item item : hit_items) {
                if (item instanceof Drop) {
                    treasure.drops++;
                    items.remove(item);
                } else if (item instanceof Gold) {
                    treasure.gold++;
                    items.remove(item);
                } else if (item instanceof Star) {
                    treasure.stars++;
                    items.remove(item);
                }
            }
        }
    }

    private void setImage() {
        int index = 0;
        if (jumping) index = 2;
        else if (running) index = 4;
        if (looking_right) index++;
        image = images[index];
    }

    public synchronized boolean isDead() {
    	return dead;
    }

    public synchronized void reset() {
    	dead = false;
        x = 0;
        y = 0;
    }

    public void render(Graphics g, int screen_x) {
        g.drawImage(image, x - screen_x, y - height, null);
    }

}
