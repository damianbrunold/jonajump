package ch.jonajump;

public class Treasure {

	public int drops;
	public int gold;
	public int stars = 3;
	
	public void levelFailed() {
		if (stars > 0) stars--;
	}

	public boolean isGameOver() {
		return stars == 0;
	}

}
