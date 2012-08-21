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

public class LevelEditorPanel extends Component {

    private static final long serialVersionUID = 1L;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private int world = 1;
    private int level = 1;

    private int x = 0;

    private boolean withBackground = true;
    private boolean withBricks = true;
    private boolean withForeground = true;
    private boolean withState = true;

    private int state = Brick.SOLID_STATE;

    private BufferedImage background_image;
    private BufferedImage bricks_image;
    private BufferedImage foreground_image;

    private Image buffer;

    private Bricks bricks = new Bricks(world, level);

    public LevelEditorPanel() throws IOException {
        loadImages();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyChar() == '1') {
                    withBackground = !withBackground;
                    repaint();
                } else if (e.getKeyChar() == '2') {
                    withBricks = !withBricks;
                    repaint();
                } else if (e.getKeyChar() == '3') {
                    withForeground = !withForeground;
                    repaint();
                } else if (e.getKeyChar() == '4') {
                    withState = !withState;
                    repaint();
                } else if (e.getKeyChar() == '4') {
                    withState = !withState;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    state = Brick.SOLID_STATE;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    state = Brick.DEADLY_STATE;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                    if (!bricks.isEmpty()) {
                        bricks.pop();
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    bricks.writeBricks(world, level);
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
                bricks.createAt(x + e.getX(), e.getY(), state);
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                bricks.updateLast(x + e.getX(), e.getY());
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                bricks.updateLast(x + e.getX(), e.getY());
                repaint();
            }
        });
        setFocusable(true);
        requestFocus();
    }

    private void loadImages() throws IOException {
        background_image = getImage("background");
        bricks_image = getImage("bricks");
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
        if (withBricks) {
            buffer_g.drawImage(bricks_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
            msg += "bricks ";
        }
        if (withForeground) {
            buffer_g.drawImage(foreground_image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
            msg += "foreground ";
        }
        if (withState) {
            renderState(buffer_g);
            msg += "state ";
        }
        if (state == Brick.SOLID_STATE) {
            msg += " / solid";
        } else if (state == Brick.DEADLY_STATE) {
            msg += " / deadly";
        }
        buffer_g.setColor(Color.BLACK);
        buffer_g.drawString(msg, 5, buffer_g.getFontMetrics().getHeight());
        buffer_g.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    private void renderState(Graphics g) {
        for (Brick element : bricks) {
            if (element.state == Brick.SOLID_STATE) {
                g.setColor(Color.CYAN);
            } else if (element.state == Brick.DEADLY_STATE) {
                g.setColor(Color.RED);
            }
            g.fillRect(element.x - x, element.y, element.width, element.height);
        }
    }

}
