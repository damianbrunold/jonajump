package ch.jonajump;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResourceLoader {

    public static BufferedImage getImage(String name) throws IOException {
        return ImageIO.read(getFile(name + ".png"));
    }

    public static int[] getOffsets(String name) throws IOException {
    	int[] result = new int[2];
        BufferedReader reader = new BufferedReader(new FileReader(getFile(name + ".txt")));
        try {
            result[0] = Integer.parseInt(reader.readLine());
            result[1] = Integer.parseInt(reader.readLine());
        } finally {
            reader.close();
        }
    	return result;
    }

    public static File getFile(String name) throws IOException {
        return new File(JonaJumpPanel.class.getResource("/resources/" + name).getFile().replace("%20", " "));
    }

}
