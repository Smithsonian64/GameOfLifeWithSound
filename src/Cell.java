import org.jfugue.player.Player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;

public class Cell {

    boolean isAlive;
    long lifeTime;
    Color color;
    static Player player = new Player();
    static {

    }

    Cell() {
        isAlive = false;
        lifeTime = 0;
        color = Color.BLACK;
    }

    public void update(long dt, boolean status) {
        if(isAlive && status) return;
        if(!isAlive && status) {
            lifeTime = 0;
            color = Color.CYAN;
            isAlive = true;
            GOLEngine.numBirths++;

           /* Runnable playSound = () -> {

            };

            Thread run = new Thread(playSound);
            run.start();*/

            return;
        }
        if(isAlive) {
            isAlive = false;
            GOLEngine.numDeaths++;
            return;
        }

        lifeTime += dt;



        color = new Color(Math.max(color.getRed() - 2, 0), Math.max(color.getGreen() - 2, 0), Math.max(color.getBlue() - 1, 0));
    }

}
