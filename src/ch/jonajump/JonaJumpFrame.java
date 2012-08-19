package ch.jonajump;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;

public class JonaJumpFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public JonaJumpFrame() throws IOException {
		super("JonaJump");
		setLayout(new BorderLayout());
		add(new JonaJumpPanel());
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) throws IOException {
		new JonaJumpFrame().setVisible(true);
	}

}
