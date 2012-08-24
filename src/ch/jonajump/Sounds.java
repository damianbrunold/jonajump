package ch.jonajump;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Sounds {

    private static Sound jump;
    private static Sound drop;
    private static Sound gold;
    private static Sound star;

    public static boolean disable_sounds = false;

    private static Deque<String> playlist = new ConcurrentLinkedDeque<String>();

    public static void init() {
        try {
            jump = loadSound("jump");
            drop = loadSound("drop");
            gold = loadSound("gold");
            star = loadSound("star");
            Thread playthread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        String sound = playlist.pollLast();
                        if (sound != null) {
                            if (sound.equals("jump")) {
                                jump.play();
                            } else if (sound.equals("drop")) {
                                drop.play();
                            } else if (sound.equals("gold")) {
                                gold.play();
                            } else if (sound.equals("star")) {
                                star.play();
                            }
                        }
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            });
            playthread.setDaemon(true);
            playthread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Sound loadSound(String name) throws Exception {
        return new Sound(ResourceLoader.getFile("sounds/" + name + ".au"));
    }

    public static void jump() {
        if (disable_sounds) return;
        //playlist.push("jump");
    }

    public static void drop() {
        if (disable_sounds) return;
        playlist.push("drop");
    }

    public static void gold() {
        if (disable_sounds) return;
        playlist.push("gold");
    }

    public static void star() {
        if (disable_sounds) return;
        playlist.push("star");
    }

}
