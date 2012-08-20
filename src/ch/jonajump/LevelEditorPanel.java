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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class LevelEditorPanel extends Component {

    private static final long serialVersionUID = 1L;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private static final int SOLID_STATE = 1;
    private static final int DEADLY_STATE = 2;

    private int x = 0;

    private boolean withBackground = true;
    private boolean withBricks = true;
    private boolean withForeground = true;
    private boolean withState = true;

    private int state = SOLID_STATE;

    private BufferedImage background;
    private BufferedImage bricks;
    private BufferedImage foreground;

    private Image buffer;

    private List<StateElement> elements = new ArrayList<StateElement>();

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
                    state = SOLID_STATE;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    state = DEADLY_STATE;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                    if (!elements.isEmpty()) {
                        elements.remove(elements.size() - 1);
                    }
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    writeState();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    x += 50;
                    if (x > background.getWidth() - SCREEN_WIDTH) {
                        x = background.getWidth() - SCREEN_WIDTH;
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
                StateElement element = new StateElement(snap(x + e.getX()), snap(e.getY()), 0, 0, state);
                elements.add(element);
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                StateElement element = elements.get(elements.size() - 1);
                element.width = snap(x + e.getX() - element.x);
                element.height = snap(e.getY() - element.y);
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                StateElement element = elements.get(elements.size() - 1);
                element.width = snap(x + e.getX() - element.x);
                element.height = snap(e.getY() - element.y);
                repaint();
            }
        });
        setFocusable(true);
        requestFocus();
        loadState();
    }

    private int snap(int i) {
        return (i + 5) / 10 * 10;
    }

    private void loadImages() throws IOException {
        background = getImage("background");
        bricks = getImage("bricks");
        foreground = getImage("foreground");
    }

    private BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(new File(LevelEditorPanel.class.getResource("/resources/" + name + ".png").getFile().replace("%20", " ")));
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
        String msg = "";
        if (withBackground) {
            buffer_g.drawImage(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
            msg += "background ";
        }
        if (withBricks) {
            buffer_g.drawImage(bricks, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
            msg += "bricks ";
        }
        if (withForeground) {
            buffer_g.drawImage(foreground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, x, 0, x + SCREEN_WIDTH, SCREEN_HEIGHT, null);
            msg += "foreground ";
        }
        if (withState) {
            renderState(buffer_g);
            msg += "state ";
        }
        if (state == SOLID_STATE) {
            msg += " / solid";
        } else if (state == DEADLY_STATE) {
            msg += " / deadly";
        }
        buffer_g.setColor(Color.BLACK);
        buffer_g.drawString(msg, 5, buffer_g.getFontMetrics().getHeight());
        buffer_g.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    private void renderState(Graphics g) {
        for (StateElement element : elements) {
            if (element.state == SOLID_STATE) {
                g.setColor(Color.CYAN);
            } else if (element.state == DEADLY_STATE) {
                g.setColor(Color.RED);
            }
            g.fillRect(element.x - x, element.y, element.width, element.height);
        }
    }

    private void loadState() {
        File file = new File("src/resources/state.txt");
        if (!file.exists()) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                String line = reader.readLine();
                while (line != null) {
                    elements.add(parseElement(line));
                    line = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StateElement parseElement(String s) {
        String[] parts = s.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int width = Integer.parseInt(parts[2]);
        int height = Integer.parseInt(parts[3]);
        int state = Integer.parseInt(parts[4]);
        return new StateElement(x, y, width, height, state);
    }

    private void writeState() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("src/resources/state.txt"));
            try {
                for (StateElement element : elements) {
                    out.println(element.toString());
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
