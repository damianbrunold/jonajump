package ch.jonajump;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;

public class JonaJumpFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JonaJumpPanel panel;

    public JonaJumpFrame() throws IOException {
        super("JonaJump");
        setLayout(new BorderLayout());
        add(panel = new JonaJumpPanel());
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                panel.stopped = true;
            }
        });
    }

    public static void main(String[] args) throws IOException {
        new JonaJumpFrame().setVisible(true);
    }

}
