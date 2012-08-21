package ch.jonajump;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JonaJumpPanel extends Component implements Runnable {

    private static final long serialVersionUID = 1L;

    public volatile boolean stopped = false;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private static final int FRAME_INTERVAL = 20;

    private static final int RUN_VELOCITY = 10;
    private static final double RUN_VELOCITY_DECAY = 0.99;
    private static final double RUN_ACCEL = 1.0;

    private static final int JUMP_ACCEL_STANDING = 15;
    private static final int JUMP_ACCEL_RUNNING = 18;
    private static final int JUMP_VELOCITY = 18;
    private static final int FALL_VELOCITY = -20;

    private static final double GRAVITY = -1.0;

    private int x = 0;

    private int player_x = 0;
    private int player_y = 0;

    private double player_velocity_x = 0;
    private double player_velocity_y = 0;
    private double player_accel_x = 0;
    private double player_accel_y = 0;

    private boolean looking_right = true;
    private boolean running = false;
    private boolean jumping = false;

    private BufferedImage background_image;
    private BufferedImage bricks_image;
    private BufferedImage foreground_image;
    private BufferedImage[] player_images = new BufferedImage[6];
    private BufferedImage player;

    private Image buffer;

    private Bricks bricks = new Bricks();

    public JonaJumpPanel() throws IOException {
        loadImages();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    looking_right = true;
                    running = true;
                    player_accel_x = RUN_ACCEL;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    looking_right = false;
                    running = true;
                    player_accel_x = RUN_ACCEL;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && !jumping) {
                    jumping = true;
                    Brick hit = bricks.hit(x + player_x + player.getWidth() / 2, player_y + 5);
                    if (hit != null && hit.state == Brick.SOLID_STATE) {
                        player_velocity_y = running ? JUMP_ACCEL_RUNNING : JUMP_ACCEL_STANDING;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    running = false;
                    player_accel_x = 0;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    running = false;
                    player_accel_x = 0;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    jumping = false;
                }
            }
        });
        setFocusable(true);
        requestFocus();
        Thread thread = new Thread(this);
        thread.start();
    }

    private void loadImages() throws IOException {
        background_image = getImage("background");
        bricks_image = getImage("bricks");
        foreground_image = getImage("foreground");
        player_images[0] = getImage("player_standing_left");
        player_images[1] = getImage("player_standing_right");
        player_images[2] = getImage("player_jumping_left");
        player_images[3]= getImage("player_jumping_right");
        player_images[4] = getImage("player_running_left");
        player_images[5]= getImage("player_running_right");
    }

    private BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(new File(JonaJumpPanel.class.getResource("/resources/" + name + ".png").getFile().replace("%20", " ")));
    }

    public Dimension getPreferredSize() {
        return new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        updateWorld(g);
    }

    public void run() {
        while (!stopped) {
            Graphics g = getGraphics();
            if (g != null) {
                updateWorld(g);
                g.dispose();
            }
            try {
                Thread.sleep(FRAME_INTERVAL);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private void updateWorld(Graphics g) {
        updateState();
        renderWorld(g);
    }

    private void updateState() {
        updatePlayerPositionX();
        updatePlayerPositionY();
        setPlayerImage();
    }

    private void updatePlayerPositionX() {
        if (player_accel_x != 0) {
            player_velocity_x = Math.min(RUN_VELOCITY, player_velocity_x + player_accel_x);
        } else {
            player_velocity_x = (int) (player_velocity_x * RUN_VELOCITY_DECAY);
        }
        if (player_velocity_x == 0) return;
        if (looking_right) {
            if (player_x < SCREEN_WIDTH / 2) {
                player_x += player_velocity_x;
                if (player_x > SCREEN_WIDTH / 2) {
                    player_x = SCREEN_WIDTH / 2;
                }
            } else {
                x += player_velocity_x;
                if (x > background_image.getWidth() - SCREEN_WIDTH) {
                    x = background_image.getWidth() - SCREEN_WIDTH;
                    player_x += player_velocity_x;
                    if (player_x > SCREEN_WIDTH - player.getWidth()) {
                        player_x = SCREEN_WIDTH - player.getWidth();
                    }
                }
            }
        } else {
            if (player_x > SCREEN_WIDTH / 2) {
                player_x -= player_velocity_x;
                if (player_x < SCREEN_WIDTH / 2) {
                    player_x = SCREEN_WIDTH / 2;
                }
            } else {
                x -= player_velocity_x;
                if (x < 0) {
                    x = 0;
                    player_x -= player_velocity_x;
                    if (player_x < 0) {
                        player_x = 0;
                    }
                }
            }
        }
    }

    private void updatePlayerPositionY() {
        player_velocity_y += player_accel_y + GRAVITY;
        player_velocity_y = Math.min(JUMP_VELOCITY, Math.max(FALL_VELOCITY, player_velocity_y));
        if (player_velocity_y != 0) {
            player_y -= player_velocity_y;
        }
        Brick hit = bricks.hit(x + player_x + (player == null ? 0 : player.getWidth() / 2), player_y + 5);
        if (hit != null) {
            player_y = hit.y;
            player_velocity_y = 0;
            player_accel_y = 0;
        }
    }

    private void setPlayerImage() {
        int index = 0;
        if (jumping) index = 2;
        else if (running) index = 4;
        if (looking_right) index++;
        player = player_images[index];
    }

    private void renderWorld(Graphics g) {
        if (buffer == null) {
            buffer = createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
        }
        Graphics buffer_g = buffer.getGraphics();
        buffer_g.drawImage(background_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        buffer_g.drawImage(bricks_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        buffer_g.drawImage(player, player_x, player_y - player.getHeight(), null);
        buffer_g.drawImage(foreground_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        buffer_g.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

}
