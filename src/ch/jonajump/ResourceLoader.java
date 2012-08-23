package ch.jonajump;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResourceLoader {

    public static BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(getFile(name + ".png"));
    }

    public static File getFile(String name) throws IOException {
        return new File(JonaJumpPanel.class.getResource("/resources/" + name).getFile().replace("%20", " "));
    }

}
