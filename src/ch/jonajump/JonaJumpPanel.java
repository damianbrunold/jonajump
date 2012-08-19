package ch.jonajump;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JonaJumpPanel extends Component {

	private static final long serialVersionUID = 1L;

	public int x = 0;
	public int y = 360;
	public BufferedImage background;
	public BufferedImage player;

	public JonaJumpPanel() throws IOException {
		background = ImageIO.read(new File(JonaJumpPanel.class.getResource("/resources/background.png").getFile().replace("%20", " ")));
		player = ImageIO.read(new File(JonaJumpPanel.class.getResource("/resources/player.png").getFile().replace("%20", " ")));
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					x += 20;
					if (x > background.getWidth() - 800) {
						x = background.getWidth() - 800;
					}
					System.out.println("scroll right");
					repaint();
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					x -= 20;
					if (x < 0) x = 0;
					System.out.println("scroll left");
					repaint();
				}
			}
		});
		setFocusable(true);
		requestFocus();
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		long start = System.nanoTime();
		g.drawImage(background, 0, 0, 800, 600, x, 0, x + 800, 600, null);
		g.drawImage(player, 400, y, null);
		long elapsed = (System.nanoTime() - start) / 1000000;
		long fps = elapsed != 0 ? 1000 / elapsed : 0;
		System.out.println("elapsed = " + elapsed + ", fps = " + fps);
	}


}
