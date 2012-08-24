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
import java.io.IOException;

import ch.jonajump.items.Brick;
import ch.jonajump.items.Drop;
import ch.jonajump.items.Gold;
import ch.jonajump.items.Item;
import ch.jonajump.items.Items;
import ch.jonajump.items.Star;

public class JonaJumpPanel extends Component implements Runnable {

    private static final long serialVersionUID = 1L;

    public volatile boolean stopped = false;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private int player_type = 1;
    private int world = 1;
    private int level = 1;

    private int background_width;

    private static final int FRAME_INTERVAL = 20;

    private int x = 0;

    private BufferedImage background_image;
    private BufferedImage foreground_image;

    private Image buffer;

    private Items bricks;
    private Player player;
    private Treasure treasure = new Treasure();

    private boolean level_failed = false;
    private boolean level_finished = false;

    public JonaJumpPanel() throws IOException {
        if (System.getProperty("disable_sounds") != null) Sounds.disable_sounds = true;
        Drop.init();
        Gold.init();
        Star.init();
        Sounds.init();
        initLevel();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                	player.startRunningRight();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                	player.startRunningLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    player.setDown(true);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && !player.jumping) {
                	player.jump();
                } else if ((level_failed || level_finished) && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    initLevel();
                } else if (System.getProperty("level_choose") != null && KeyEvent.VK_1 <= e.getKeyCode() && e.getKeyCode() <= KeyEvent.VK_9) {
                    level = e.getKeyCode() - KeyEvent.VK_0;
                    initLevel();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                	player.stopRunning();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                	player.stopRunning();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    player.setDown(false);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    player.jumping = false;
                }
            }
        });
        setFocusable(true);
        requestFocus();
        Thread thread = new Thread(this);
        thread.start();
    }

    private void initLevel() {
        try {
            if (level_failed && treasure.isGameOver()) {
            	treasure = new Treasure();
            	level = 1;
            }
            if (level_finished) level++;
            loadImages();
            Brick.init(world, level);
            bricks = new Items(world, level);
            background_width = background_image.getWidth();
            player = new Player(player_type, bricks, treasure, background_width, SCREEN_HEIGHT);
            x = 0;
            level_failed = level_finished = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImages() throws IOException {
        background_image = ResourceLoader.getImage("world" + world + "/level" + level + "/background");
        foreground_image = ResourceLoader.getImage("world" + world + "/level" + level + "/foreground");
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
        if (level_failed || level_finished) return;
        player.move();
        updateScreenPosition();
        checkGameOver();
        checkLevelFinished();
    }

    private void updateScreenPosition() {
        x = Math.min(Math.max(player.x - SCREEN_WIDTH / 2, 0), background_width - SCREEN_WIDTH);
    }

    private void checkGameOver() {
        if (player.isDead()) {
        	treasure.levelFailed();
        	level_failed = true;
        }
    }

    private void checkLevelFinished() {
        if (player.x > background_width - player.width - 10) {
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
        player.render(buffer_g, x);
        buffer_g.drawImage(foreground_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
        drawInfos(buffer_g);
        if (level_failed) {
        	if (treasure.isGameOver()) {
        		drawMessage(buffer_g, "Level Failed - Game Over");
        	} else {
        		drawMessage(buffer_g, "Level Failed");
        	}
        } else if (level_finished) {
            drawMessage(buffer_g, "Level Finished");
        }
        buffer_g.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    private void renderBricks(Graphics g) {
        for (Item brick : bricks) {
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
        String found = "Drops " + treasure.drops + ", Gold " + treasure.gold + ", Stars " + treasure.stars;
        drawString(g, SCREEN_WIDTH - 10 - g.getFontMetrics().stringWidth(found), 10 + g.getFontMetrics().getHeight(), found);
    }

    private void drawString(Graphics g, int x, int y, String str) {
        g.setColor(Color.WHITE);
        g.drawString(str, x + 1, y + 1);
        g.setColor(Color.BLACK);
        g.drawString(str, x, y);
    }
}
