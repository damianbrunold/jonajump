package ch.jonajump;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.jonajump.items.Brick;
import ch.jonajump.items.Drop;
import ch.jonajump.items.Gold;
import ch.jonajump.items.Item;
import ch.jonajump.items.Items;
import ch.jonajump.items.Star;

public class LevelEditorPanel extends Component {

    private static final long serialVersionUID = 1L;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private int world = 1;
    private int level = 1;

    private int x = 0;

    private boolean withBackground = true;
    private boolean withForeground = true;
    private boolean withGrid = true;
    private boolean withItems = true;

    private int state = 1;

    private BufferedImage background_image;
    private BufferedImage foreground_image;

    private Image buffer;

    private Items items;

    public LevelEditorPanel() throws IOException {
        Drop.init();
        Gold.init();
        Star.init();
        initLevel();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyChar() == '1') {
                    withBackground = !withBackground;
                    repaint();
                } else if (e.getKeyChar() == '2') {
                    withForeground = !withForeground;
                    repaint();
                } else if (e.getKeyChar() == '3') {
                    withItems = !withItems;
                    repaint();
                } else if (e.getKeyChar() == '4') {
                    withGrid = !withGrid;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_B) {
                    state = 1;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    state = 2;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_G) {
                    state = 3;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    state = 4;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    level++;
                    try {
                        initLevel();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    level--;
                    try {
                        initLevel();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                    if (!items.isEmpty()) {
                        items.pop();
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    items.writeItems(world, level);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    x += 50;
                    if (x > background_image.getWidth() - SCREEN_WIDTH) {
                        x = background_image.getWidth() - SCREEN_WIDTH;
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    x -= 50;
                    if (x < 0) {
                        x = 0;
                    }
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                items.createAt(x + e.getX(), e.getY(), state);
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                items.updateLast(x + e.getX(), e.getY());
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                items.updateLast(x + e.getX(), e.getY());
                repaint();
            }
        });
        setFocusable(true);
        requestFocus();
    }

    private void initLevel() throws IOException {
        loadImages();
        Brick.init(world, level);
        items = new Items(world, level);
        buffer = null;
        x = 0;
    }

    private void loadImages() throws IOException {
        background_image = getImage("background");
        foreground_image = getImage("foreground");
    }

    private BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(new File(LevelEditorPanel.class.getResource("/resources/world" + world + "/level" + level + "/" + name + ".png").getFile().replace("%20", " ")));
    }

    public Dimension getPreferredSize() {
        return new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        renderWorld(g);
    }

    private void renderWorld(Graphics g) {
        if (buffer == null) {
            buffer = createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
        }
        Graphics buffer_g = buffer.getGraphics();
        buffer_g.setColor(Color.WHITE);
        buffer_g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        String msg = "world " + world + "level " + level + ": ";
        if (withBackground) {
            buffer_g.drawImage(background_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
            msg += "background ";
        }
        if (withItems) {
            renderItems(buffer_g);
            msg += "items ";
        }
        if (withForeground) {
            buffer_g.drawImage(foreground_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
            msg += "foreground ";
        }
        if (withGrid) {
            renderGrid(buffer_g);
            msg += "grid ";
        }
        if (state == 1) {
            msg += " / brick";
        } else if (state == 2) {
            msg += " / drop";
        } else if (state == 3) {
            msg += " / gold";
        } else if (state == 4) {
            msg += " / star";
        }
        buffer_g.setColor(Color.BLACK);
        buffer_g.drawString(msg, 5, buffer_g.getFontMetrics().getHeight());
        buffer_g.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    private void renderItems(Graphics g) {
        for (Item item : items) {
            item.render(g, x, x + SCREEN_WIDTH);
        }
    }

    private void renderGrid(Graphics g) {
        g.setColor(new Color(128, 128, 128, 128));
        for (int x = 0; x <  SCREEN_WIDTH; x += 10) {
            g.drawLine(x, 0, x, SCREEN_HEIGHT);
        }
        for (int y = 0; y <  SCREEN_HEIGHT; y += 10) {
            g.drawLine(0, y, SCREEN_WIDTH, y);
        }
    }

}
