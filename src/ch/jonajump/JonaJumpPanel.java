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

	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	
	private static final int JUMP_VELOCITY = 25;
	private static final int ACCEL_Y = 100;
	
	public volatile boolean stopped = false;
	
	private int x = 0;
	private int y = 360; // TODO
	private int player_offset_x = -SCREEN_WIDTH / 2;
	private int player_offset_y = 0;
	private int player_accel_y = 0;
	private int jump_time = 0;
	private int running_time = 0;
	
	private boolean looking_right = true;
	private boolean running = false;
	private boolean jumping = false;

	private BufferedImage background;
	private BufferedImage player_standing_right;
	private BufferedImage player_standing_left;
	private BufferedImage player_running_right;
	private BufferedImage player_running_left;
	private BufferedImage player_jumping_right;
	private BufferedImage player_jumping_left;
	private BufferedImage player;

	public JonaJumpPanel() throws IOException {
		loadImages();
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					looking_right = true;
					running = true;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					looking_right = false;
					running = true;
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					jumping = true;
					jump_time = 0;
					player_accel_y = 1;
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					running = false;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					running = false;
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
		player_standing_right = getImage("player_standing_right");
		player_standing_left = getImage("player_standing_left");
		player_running_right = getImage("player_running_right");
		player_running_left = getImage("player_running_left");
		player_jumping_right = player_running_right; // TODO 
		player_jumping_left = player_running_left; // TODO 
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
				Thread.sleep(20);
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
		if (running) {
			if (looking_right) {
				if (player_offset_x < 0) {
					player_offset_x += 10;
					if (player_offset_x > 0) {
						player_offset_x = 0;
					}
				} else {
					x += 10;
					if (x > background.getWidth() - SCREEN_WIDTH) {
						x = background.getWidth() - SCREEN_WIDTH;
						player_offset_x += 10;
						if (player_offset_x > SCREEN_WIDTH / 2 - player.getWidth()) {
							player_offset_x = SCREEN_WIDTH / 2 - player.getWidth() ;
						}
					}
				}
			} else {
				if (player_offset_x > 0) {
					player_offset_x -= 10;
					if (player_offset_x < 0) {
						player_offset_x = 0;
					}
				} else {
					x -= 10;
					if (x < 0) {
						x = 0;
						player_offset_x -= 10;
						if (player_offset_x < -SCREEN_WIDTH / 2) {
							player_offset_x = -SCREEN_WIDTH / 2;
						}
					}
				}
			}
		}
	}

	private void updatePlayerPositionY() {
		if (jumping) {
			player_offset_y = JUMP_VELOCITY * jump_time - player_accel_y * jump_time * jump_time;
			jump_time++;
			if (player_offset_y < 0) {
				player_offset_y = 0;
				player_accel_y = 0;
				jumping = false; 
			}
		}
	}

	private void setPlayerImage() {
		if (running) {
			if (looking_right) player = player_running_right;
			else player = player_running_left;
		} else if (jumping) {
			if (looking_right) player = player_jumping_right;
			else player = player_jumping_left;
		} else {
			if (looking_right) player = player_standing_right;
			else player = player_standing_left;
		}
	}

	private void renderWorld(Graphics g) {
		g.drawImage(background, 0, 0, 800, 600, x, 0, x + 800, 600, null);
		g.drawImage(player, 400 + player_offset_x, y - player_offset_y, null);
	}

}
