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
    private static final double RUN_VELOCITY_DECAY = 0.9;
    private static final double ACCEL_X = 1.0;

    private static final int JUMP_VELOCITY_FAST_MAX = 25;
    private static final int JUMP_VELOCITY_FAST = 15;
    private static final int JUMP_VELOCITY_SLOW_MAX = 20;
    private static final int JUMP_VELOCITY_SLOW = 12;
    private static final double ACCEL_Y = 1.0;

    private int x = 0;
    private int y = 360; // TODO

    private int player_x = 0;
    private int player_y = 0;

    private double player_velocity_x = 0;
    private double player_velocity_y = 0;
    private double player_velocity_y_max = 0;
    private double player_accel_x = 0;
    private double player_accel_y = 0;

    private int jump_time = 0;

    private boolean looking_right = true;
    private boolean running = false;
    private boolean jumping = false;

    private BufferedImage background;
    private BufferedImage bricks;
    private BufferedImage foreground;
    private BufferedImage[] player_images = new BufferedImage[6];
    private BufferedImage player;

    private Image buffer;

    public JonaJumpPanel() throws IOException {
        loadImages();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    looking_right = true;
                    running = true;
                    player_accel_x = ACCEL_X;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    looking_right = false;
                    running = true;
                    player_accel_x = ACCEL_X;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && !jumping) {
                    jumping = true;
                    if (jump_time == 0) {
                        player_accel_y = ACCEL_Y;
                        player_velocity_y = isRunning() ? JUMP_VELOCITY_FAST : JUMP_VELOCITY_SLOW;
                        player_velocity_y_max = isRunning() ? JUMP_VELOCITY_FAST_MAX : JUMP_VELOCITY_SLOW_MAX;
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
        background = getImage("background");
        bricks = getImage("bricks");
        foreground = getImage("foreground");
        player_images[0] = getImage("player_standing_left");
        player_images[1] = getImage("player_standing_right");
        player_images[2] = getImage("player_jumping_left");
        player_images[3]= getImage("player_jumping_right");
        player_images[4] = getImage("player_running_left");
        player_images[5]= getImage("player_running_right");
    }

    private boolean isJumping() {
        return jumping;
    }

    private boolean isRunning() {
        return running;
    }

    private boolean isStanding() {
        return !running && !jumping;
    }

    private boolean isLeft() {
        return !isRight();
    }

    private boolean isRight() {
        return looking_right;
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
                if (x > background.getWidth() - SCREEN_WIDTH) {
                    x = background.getWidth() - SCREEN_WIDTH;
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
        if (jumping) {
            player_velocity_y = jump_time > 2 ? player_velocity_y_max : player_velocity_y;
        }
        if (player_velocity_y != 0) {
            player_y = (int) (player_velocity_y * jump_time - player_accel_y * jump_time * jump_time);
            jump_time++;
        }
        if (player_y < 0) {
            player_y = 0;
            player_velocity_y = 0;
            player_accel_y = 0;
            jump_time = 0;
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
        buffer_g.drawImage(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        buffer_g.drawImage(bricks, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        buffer_g.drawImage(player, player_x, y - player_y, null);
        buffer_g.drawImage(foreground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        buffer_g.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

}
