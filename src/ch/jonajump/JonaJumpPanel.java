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

	private static final int JUMP_VELOCITY = 25;
	private static final int ACCEL_Y = 100;
	
	public volatile boolean stopped = false;
	
	private int x = 0;
	private int y = 360;
	private int player_offset_x = -400;
	private int player_offset_y = 0;
	private int player_accel_y = 0;
	private int jump_time = 0;
	private BufferedImage background;
	private BufferedImage player_standing_right;
	private BufferedImage player_standing_left;
	private BufferedImage player_running_right;
	private BufferedImage player_running_left;
	private BufferedImage player_jumping_right;
	private BufferedImage player_jumping_left;
	private BufferedImage player;
	private PlayerState playerState = PlayerState.STANDING_RIGHT;

	public JonaJumpPanel() throws IOException {
		background = getImage("background");
		player_standing_right = getImage("player_standing_right");
		player_standing_left = getImage("player_standing_left");
		player_running_right = getImage("player_running_right");
		player_running_left = getImage("player_running_left");
		player_jumping_right = player_running_right; // TODO 
		player_jumping_left = player_running_left; // TODO 
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (isJumping()) {
						playerState = PlayerState.JUMPING_RIGHT;
					} else {
						playerState = PlayerState.RUNNING_RIGHT;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (isJumping()) {
						playerState = PlayerState.JUMPING_LEFT;
					} else {
						playerState = PlayerState.RUNNING_LEFT;
					}
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					playerState = isRight() ? PlayerState.JUMPING_RIGHT : PlayerState.JUMPING_LEFT;
					jump_time = 0;
					player_accel_y = 1;
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
		return playerState == PlayerState.JUMPING_LEFT || 
				playerState == PlayerState.JUMPING_RIGHT; 
	}
	
	private boolean isRunning() {
		return playerState == PlayerState.RUNNING_LEFT || 
				playerState == PlayerState.RUNNING_RIGHT; 
	}
	
	private boolean isStanding() {
		return playerState == PlayerState.STANDING_LEFT || 
				playerState == PlayerState.STANDING_RIGHT; 
	}
	
	private boolean isLeft() {
		return playerState == PlayerState.STANDING_LEFT || 
				playerState == PlayerState.RUNNING_LEFT ||
				playerState == PlayerState.JUMPING_LEFT; 
	}
	
	private boolean isRight() {
		return playerState == PlayerState.STANDING_RIGHT || 
				playerState == PlayerState.RUNNING_RIGHT ||
				playerState == PlayerState.JUMPING_RIGHT; 
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
		switch (playerState) {
		case RUNNING_RIGHT:
			if (isLeft()) {
				player = player_standing_right;
			} else {
				player = player_running_right;
				if (player_offset_x < 0) {
					player_offset_x += 10;
					if (player_offset_x > 0) {
						player_offset_x = 0;
					}
				} else {
					x += 10;
					if (x > background.getWidth() - getWidth()) {
						x = background.getWidth() - getWidth();
						player_offset_x += 10;
						if (player_offset_x > 400 - player.getWidth()) {
							player_offset_x = 400 - player.getWidth() ;
						}
					}
				}
			}
			break;
			
		case RUNNING_LEFT:
			if (isRight()) {
				player = player_standing_left;
			} else {
				player = player_running_left;
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
						if (player_offset_x < -400) {
							player_offset_x = -400;
						}
					}
				}
			}
			break;
			
		case STANDING_RIGHT:
			player = player_standing_right;
			break;

		case STANDING_LEFT:
			player = player_standing_left;
			break;
			
		case JUMPING_RIGHT:
			player_offset_y = JUMP_VELOCITY * jump_time - player_accel_y * jump_time * jump_time;
			jump_time++;
			if (player_offset_y < 0) {
				player_offset_y = 0;
				player_accel_y = 0;
				playerState = PlayerState.STANDING_RIGHT; 
			}
			break;

		case JUMPING_LEFT:
			player_offset_y = JUMP_VELOCITY * jump_time - player_accel_y * jump_time * jump_time;
			jump_time++;
			if (player_offset_y < 0) {
				player_offset_y = 0;
				player_accel_y = 0;
				playerState = PlayerState.STANDING_LEFT; 
			}
			break;
		}
	}

	private void renderWorld(Graphics g) {
		g.drawImage(background, 0, 0, 800, 600, x, 0, x + 800, 600, null);
		g.drawImage(player, 400 + player_offset_x, y - player_offset_y, null);
	}

}
