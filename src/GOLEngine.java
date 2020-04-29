import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class GOLEngine extends JFrame {

    Cell[][] cells;
    boolean[][] cellStep;

    static int windowWidth;
    static int windowHeight;
    static long lastTime;
    static int numBirths;
    static int numDeaths;

    public static float baselineFrequency = 365;
    public static float baselineAmplitude = 0.4f;


    static BufferedImage grid;
    JPanel imagePanel;

    static Synthesizer synth = JSyn.createSynthesizer();;
    static UnitOscillator osc1;
    static UnitOscillator osc2;
    static LineOut lineOut;

    public GOLEngine(int width, int height) {
        super("GOL");

        windowWidth = width;
        windowHeight = height;



        this.setSize(width, height);
        imagePanel = new JPanel();

        grid = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_RGB);

        cells = new Cell[grid.getWidth()][grid.getHeight()];
        cellStep = new boolean[grid.getWidth()][grid.getHeight()];
        for(int i = 0; i < cellStep.length; i++) {
            for(int j = 0; j < cellStep[i].length; j++) {
                cellStep[i][j] = false;
                cells[i][j] = new Cell();
            }
        }
        for(int i = 0; i < 50; i++) {
            for(int j = 0; j < 50; j++) {
                if(Math.random() >= 0.5) cells[i + grid.getWidth() / 2 - 25][j + grid.getHeight() / 2 - 25].isAlive = true;
            }
        }

        lastTime = System.nanoTime() / 1000000;

        /*cells[100][100].isAlive = true;
        cells[100][101].isAlive = true;
        cells[100][102].isAlive = true;*/
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
        g2d.drawImage(grid,0,0, windowWidth, windowHeight, null);
        g.drawImage(output, 0, 0, null);

    }

    public BufferedImage getGridImage() {
        BufferedImage temp = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[i].length; j++) {
                temp.setRGB(i, j, cells[i][j].color.getRGB());
                //else temp.setRGB(i, j, Color.BLACK.getRGB());
            }
        }

        return temp;

    }

    public void calculateNextStep() {
        long currentTime = System.nanoTime() / 1000000;
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[i].length; j++) {
                cellStep[i][j] = checkNeighbors(i, j);
            }
        }
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[i].length; j++) {
                cells[i][j].update(currentTime - lastTime, cellStep[i][j]);
            }
        }

        osc1.frequency.set(baselineFrequency * numBirths / numDeaths);
//        osc2.frequency.set(baselineFrequency * numBirths / numDeaths);

        //if(numDeaths + numBirths >= 100) osc1.amplitude.set(baselineAmplitude);


        System.out.println(numBirths);
        System.out.println(numDeaths);

        numDeaths = 0;
        numBirths = 0;

        //if(numDeaths == 0 && numBirths == 0) synth.stop();



    }

    public boolean isNeighborAlive(int x, int y) {
        boolean temp = false;

        if(x - 1 < 0 || x + 1 >= grid.getWidth()) return false;
        if(y - 1 < 0 || y + 1 >= grid.getHeight()) return false;

        if(cells[x + 1][y].isAlive) temp = true;
        if(cells[x + 1][y + 1].isAlive) temp = true;
        if(cells[x][y + 1].isAlive) temp = true;
        if(cells[x - 1][y + 1].isAlive) temp = true;
        if(cells[x - 1][y].isAlive) temp = true;
        if(cells[x - 1][y - 1].isAlive) temp = true;
        if(cells[x][y - 1].isAlive) temp = true;
        if(cells[x + 1][y - 1].isAlive) temp = true;

        return temp;
    }

    public boolean checkNeighbors(int x, int y) {
        int count = 0;

        if(x - 1 < 0 ||  y - 1 < 0 || x + 1 >= cells.length || y + 1 >= cells[x].length) return false;

        if(cells[x + 1][y].isAlive) count++;
        if(cells[x + 1][y + 1].isAlive) count++;
        if(cells[x][y + 1].isAlive) count++;
        if(cells[x - 1][y + 1].isAlive) count++;
        if(cells[x - 1][y].isAlive) count++;
        if(cells[x - 1][y - 1].isAlive) count++;
        if(cells[x][y - 1].isAlive) count++;
        if(cells[x + 1][y - 1].isAlive) count++;

        if(count == 0) return false;
        if(!cells[x][y].isAlive && count == 3) return true;
        else return cells[x][y].isAlive && count == 2 || count == 3;

    }

    public void doIterations(int count){
        Runnable animate = () -> {
            int num = 0;
            while (count < 0 || num < count) {
                calculateNextStep();
                grid = getGridImage();
                repaint();
                try {
                    Thread.sleep(1);
                } catch (Exception ignored) {

                }
                num++;
            }





        };

        Thread run = new Thread(animate);
        run.start();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> {
                    GOLEngine window1 = new GOLEngine((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
                    window1.doIterations(-1);
                });
    }


}
