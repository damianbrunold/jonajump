package ch.jonajump;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
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

    private int player = 1;
    private int world = 1;
    private int level = 1;

    private int background_width;
    private int player_width;
    private int player_height;

    private static final int FRAME_INTERVAL = 20;

    private static final int RUN_VELOCITY = 10;
    private static final double RUN_ACCEL = 1.0;

    private static final double AIR_VELOCITY_DECAY = 0.92;
    private static final double GROUND_VELOCITY_DECAY = 0.5;

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
    private boolean down = false;

    private BufferedImage background_image;
    private BufferedImage foreground_image;
    private BufferedImage[] player_images = new BufferedImage[6];
    private BufferedImage player_image;
    private Image buffer;

    private Bricks bricks = new Bricks(world, level);

    private boolean game_over = false;
    private boolean level_finished = false;

    public JonaJumpPanel() throws IOException {
        loadImages();
        background_width = background_image.getWidth();
        player_width = player_image.getWidth();
        player_height = player_image.getHeight();
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
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    down = true;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && !jumping) {
                    jumping = true;
                    Brick hit = bricks.hit(player_x + player_width / 2, player_y + 2);
                    if (hit != null && hit.state == Brick.SOLID_STATE) {
                        player_velocity_y = running ? JUMP_ACCEL_RUNNING : JUMP_ACCEL_STANDING;
                        if (down) {
                            player_velocity_y *= -1;
                            player_y += 5;
                        }
                    }
                } else if ((game_over || level_finished) && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    x = 0;
                    player_x = 0;
                    player_y = 0;
                    game_over = level_finished = false;
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
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    down = false;
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
        background_image = getImage("world" + world + "/level" + level + "/background");
        foreground_image = getImage("world" + world + "/level" + level + "/foreground");
        player_images[0] = getImage("player" + player + "/standing_left");
        player_images[1] = getImage("player" + player + "/standing_right");
        player_images[2] = getImage("player" + player + "/jumping_left");
        player_images[3]= getImage("player" + player + "/jumping_right");
        player_images[4] = getImage("player" + player + "/running_left");
        player_images[5]= getImage("player" + player + "/running_right");
        player_image = player_images[1];
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
        updateScreenPosition();
        checkLevelFinished();
        setPlayerImage();
    }

    private void updatePlayerPositionX() {
        if (game_over || level_finished) return;
        if (player_accel_x != 0) {
            player_velocity_x = Math.min(RUN_VELOCITY, player_velocity_x + player_accel_x);
        } else if (bricks.hit(player_x + player_width / 2, player_y + 1) == null) {
            player_velocity_x *= AIR_VELOCITY_DECAY;
            if (player_velocity_x < 1) player_velocity_x = 0;
        } else {
            player_velocity_x *= GROUND_VELOCITY_DECAY;
            if (player_velocity_x < 1) player_velocity_x = 0;
        }
        if (player_velocity_x == 0) return;
        if (looking_right) {
            player_x = Math.min((int) (player_x + player_velocity_x), background_width);
        } else {
            player_x = Math.max((int) (player_x - player_velocity_x), 0);
        }
    }

    private void updatePlayerPositionY() {
        if (game_over || level_finished) return;
        player_velocity_y += player_accel_y + GRAVITY;
        player_velocity_y = Math.min(JUMP_VELOCITY, Math.max(FALL_VELOCITY, player_velocity_y));
        if (player_velocity_y != 0) {
            player_y -= player_velocity_y;
        }
        Brick hit = bricks.hit(player_x + player_width / 2, player_y + 1);
        if (hit != null) {
            if (player_velocity_y < 0) {
                player_y = hit.y;
                player_velocity_y = 0;
                player_accel_y = 0;
            }
            if (hit.state == Brick.DEADLY_STATE) game_over = true;
        }
        if (player_y > SCREEN_HEIGHT) game_over = true;
    }

    private void updateScreenPosition() {
        x = Math.min(Math.max(player_x - SCREEN_WIDTH / 2, 0), background_width - SCREEN_WIDTH);
    }

    private void setPlayerImage() {
        int index = 0;
        if (jumping) index = 2;
        else if (running) index = 4;
        if (looking_right) index++;
        player_image = player_images[index];
    }

    private void checkLevelFinished() {
        if (player_x > background_width - player_width - 10) {
            level_finished = true;
        }
    }

    private void renderWorld(Graphics g) {
        if (buffer == null) {
            buffer = createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
        }
        Graphics buffer_g = buffer.getGraphics();
        buffer_g.drawImage(background_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        renderBricks(buffer_g);
        buffer_g.drawImage(player_image, player_x - x, player_y - player_height, null);
        buffer_g.drawImage(foreground_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        drawInfos(buffer_g);
        if (game_over) {
            drawMessage(buffer_g, "Game Over");
        } else if (level_finished) {
            drawMessage(buffer_g, "Level Finished");
        }
        buffer_g.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    private void renderBricks(Graphics g) {
        for (Brick brick : bricks) {
            brick.render(g, x, x + SCREEN_WIDTH);
        }
    }

    private void drawMessage(Graphics g, String msg) {
        g.setColor(Color.WHITE);
        g.fillRect(300, 275, 200, 50);
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, 400 - fm.stringWidth(msg) / 2, 300 + fm.getAscent() / 2);
    }

    private void drawInfos(Graphics g) {
        drawString(g, 10, 10 + g.getFontMetrics().getHeight(), "World " + world + ", Level " + level);
    }

    private void drawString(Graphics g, int x, int y, String str) {
        g.setColor(Color.WHITE);
        g.drawString(str, x + 1, y + 1);
        g.setColor(Color.BLACK);
        g.drawString(str, x, y);
    }
}
