package ch.jonajump;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;

public class LevelEditor extends JFrame {

    private static final long serialVersionUID = 1L;

    public LevelEditor() throws IOException {
        super("JonaJump Level Editor");
        setLayout(new BorderLayout());
        add(new LevelEditorPanel());
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) throws IOException {
        new LevelEditor().setVisible(true);
    }

}
