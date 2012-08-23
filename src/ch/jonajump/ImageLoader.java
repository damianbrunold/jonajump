package ch.jonajump;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {

    public static BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(new File(JonaJumpPanel.class.getResource("/resources/" + name + ".png").getFile().replace("%20", " ")));
    }

}
