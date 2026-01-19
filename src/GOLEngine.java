import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class GOLEngine extends JFrame {

    boolean[][] cellStep;

    static int windowWidth;
    static int windowHeight;
    static long lastTime;
    static int numBirths;
    static int numDeaths;

    public static float baselineFrequency = 150;
    public static float baselineAmplitude = 0.15f;


    static BufferedImage gridimage;
    JPanel imagePanel;

    static Synthesizer synth = JSyn.createSynthesizer();;
    static UnitOscillator osc1;
    static UnitOscillator osc2;
    static LineOut lineOut;

    public Grid grid;

    float trend;

    public GOLEngine(int width, int height, Grid grid) {
        super("GOL");

        windowWidth = width;
        windowHeight = height;



        this.setSize(width, height);

        trend = 0;
        this.grid = grid;

        gridimage = new BufferedImage(grid.width, grid.height, BufferedImage.TYPE_INT_RGB);

        lastTime = System.nanoTime() / 1000000000;//1000000000

        Runnable playSound = () -> {
            synth.start();
            synth.add(osc1 = new SineOscillator());
            //synth.add(osc2 = new SineOscillatorPhaseModulated());
            synth.add(lineOut = new LineOut());
            osc1.output.connect(0, lineOut.input, 0);
            osc1.output.connect(0, lineOut.input, 1);
            //osc2.output.connect(0, lineOut.input, 0);
            //osc2.output.connect(0, lineOut.input, 1);
            osc1.frequency.set(baselineFrequency);
            osc1.amplitude.set(baselineAmplitude);
            //osc2.frequency.set(baselineFrequency);
            //osc2.amplitude.set(baselineAmplitude);
            lineOut.start();
        };

        Thread run = new Thread(playSound);
        run.start();




        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setVisible(true);


        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                System.exit(0);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

    }

    @Override
    public void paint(Graphics g) {

        BufferedImage output = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.drawImage(gridimage,0,0, windowWidth, windowHeight, null);
        g.drawImage(output, 0, 0, null);

    }

    public BufferedImage getGridImage() {
        BufferedImage temp = new BufferedImage(gridimage.getWidth(), gridimage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < gridimage.getWidth(); i++) {
            for(int j = 0; j < gridimage.getHeight(); j++) {
                temp.setRGB(i, j, (grid.getColor(i,j)).getRGB());
            }
        }

        return temp;

    }

    public void calculateNextStep(Grid grid) {
        long time = System.nanoTime();



        grid.step();




        osc1.frequency.set(baselineFrequency * grid.births / grid.deaths);

        numDeaths = 0;
        numBirths = 0;

        long dt = System.nanoTime() - time;
        while(dt < 16666667L) {//1000000000//16666667L//166666667L
            dt = System.nanoTime() - time;
        };

    }

    public void doIterations(int count, Grid grid){
        Runnable animate = () -> {
            int num = 0;
            while (count < 0 || num < count) {
                calculateNextStep(grid);
                gridimage = getGridImage();
                repaint();
                try {
                    //Thread.sleep(1);
                } catch (Exception ignored) {

                }
                num++;
            }

        };

        Thread run = new Thread(animate);
        run.start();

    }

    public static void main(String[] args) {
        Grid grid = new Grid((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight(), 4);
        grid.randomize();
        SwingUtilities.invokeLater(
                () -> {
                    GOLEngine window1 = new GOLEngine((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()), grid);
                    window1.doIterations(-1, grid);
                });
    }


}
