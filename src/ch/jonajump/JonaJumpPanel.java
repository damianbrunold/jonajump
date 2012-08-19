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

public class JonaJumpPanel extends Component implements Runnable {

	private static final long serialVersionUID = 1L;

	private int x = 0;
	private int y = 360;
	private BufferedImage background;
	private BufferedImage player_standing_right;
	private BufferedImage player_standing_left;
	private BufferedImage player_running_right;
	private BufferedImage player_running_left;
	private BufferedImage player;
	private PlayerState playerState = PlayerState.STANDING_RIGHT;

	public JonaJumpPanel() throws IOException {
		background = getImage("background");
		player_standing_right = getImage("player_standing_right");
		player_standing_left = getImage("player_standing_left");
		player_running_right = getImage("player_running_right");
		player_running_left = getImage("player_running_left");
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (isJumping()) {
						playerState = PlayerState.JUMPING_RIGHT;
					} else {
						playerState = PlayerState.RUNNING_RIGHT;
						player = player_running_right;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (isJumping()) {
						playerState = PlayerState.JUMPING_LEFT;
					} else {
						playerState = PlayerState.RUNNING_LEFT;
						player = player_running_left;
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (isJumping()) {
						playerState = PlayerState.JUMPING_RIGHT;
					} else {
						playerState = PlayerState.STANDING_RIGHT;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (isJumping()) {
						playerState = PlayerState.JUMPING_LEFT;
					} else {
						playerState = PlayerState.STANDING_LEFT;
					}
				}
			}
		});
		setFocusable(true);
		requestFocus();
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private boolean isJumping() {
		return playerState == PlayerState.JUMPING_LEFT || playerState == PlayerState.JUMPING_RIGHT; 
	}
	
	private boolean isRunning() {
		return playerState == PlayerState.RUNNING_LEFT || playerState == PlayerState.RUNNING_RIGHT; 
	}
	
	private boolean isStanding() {
		return playerState == PlayerState.STANDING_LEFT || playerState == PlayerState.STANDING_RIGHT; 
	}
	
	private BufferedImage getImage(String name) throws IOException {
		return ImageIO.read(new File(JonaJumpPanel.class.getResource("/resources/" + name + ".png").getFile().replace("%20", " ")));
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		updateWorld(g);
	}

	public void run() {
		while (true) {
			Graphics g = getGraphics();
			if (g != null) {
				updateWorld(g);
				g.dispose();
			}
			try {
				Thread.sleep(33);
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
		switch (playerState) {
		case RUNNING_RIGHT:
			player = player_running_right;
			x += 20;
			if (x > background.getWidth() - 600) {
				x = background.getWidth() - 600;
			}
			break;
			
		case RUNNING_LEFT:
			player = player_running_left;
			x -= 20;
			if (x < 0) {
				x = 0;
			}
			break;
			
		case STANDING_RIGHT:
			player = player_standing_right;
			break;

		case STANDING_LEFT:
			player = player_standing_left;
			break;
		}
	}

	private void renderWorld(Graphics g) {
		long start = System.nanoTime();
		g.drawImage(background, 0, 0, 800, 600, x, 0, x + 800, 600, null);
		g.drawImage(player, 400, y, null);
		long elapsed = System.nanoTime() - start;
		System.out.println("elapsed = " + elapsed);
	}

}
