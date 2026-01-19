import java.awt.*;
import java.util.Random;

public class Grid {

    private boolean[][] gridlife;
    private Color[][] gridcolor;
    public int width;
    public int height;
    public int births;
    public int deaths;
    public int scale;
    public Grid(int sizeX, int sizeY, int scale) {
        gridlife = new boolean[sizeX/scale][sizeY/scale];
        gridcolor = new Color[sizeX/scale][sizeY/scale];
        for (Color[] row : gridcolor) {
            java.util.Arrays.fill(row, Color.BLACK);
        }
        this.scale = scale;
        width = sizeX/scale;
        height = sizeY/scale;
    }

    public boolean isAlive(int x, int y) {
        return gridlife[x][y];
    }

    public void setColor(int x, int y, Color color) {
        gridcolor[x][y] = color;
    }

    public void stepColor(int x, int y) {
        int rate = 5;
        if(isAlive(x,y)){gridcolor[x][y] = Color.MAGENTA; return;}
            int red = Math.max(gridcolor[x][y].getRed() - rate, 0);
            int green = Math.max(gridcolor[x][y].getGreen() - rate, 0);
            int blue = Math.max(gridcolor[x][y].getBlue() - rate, 0);
            gridcolor[x][y] = new Color(red,green,blue,0);
    }

    public Color getColor(int x, int y) {
        return gridcolor[x][y];
    }

    public void step() {
        boolean[][] newgridlife = new boolean[width][height];

        for(int i = 0; i < gridlife.length; i++)
            newgridlife[i] = gridlife[i].clone();

        births = 0;
        deaths = 0;

        for(int i = 0; i < width - 1; i++){
            for(int j = 0; j < height - 1; j++) {
                if(isAlive(i,j) && aliveNeighbors(i,j) < 2) {newgridlife[i][j] = false; deaths++; stepColor(i,j); continue;}
                if(isAlive(i,j) && aliveNeighbors(i,j) > 3) {newgridlife[i][j] = false; deaths++; stepColor(i,j); continue;}
                if(!isAlive(i,j) && aliveNeighbors(i,j) == 3) {newgridlife[i][j] = true; births++; stepColor(i,j); continue;}
                stepColor(i,j);
            }
        }
        gridlife = newgridlife;
    }

    public int aliveNeighbors(int x, int y) {
        if(x - 1 < 0) return 0;
        if(y - 1 < 0) return 0;
        if(x + 1 > width - 1) return 0;
        if(y + 1 > height - 1) return 0;
        int num = 0;
        num += isAlive(x-1, y-1) ? 1 : 0;
        num += isAlive(x-1, y) ? 1 : 0;
        num += isAlive(x-1, y+1) ? 1 : 0;
        num += isAlive(x, y-1) ? 1 : 0;
        num += isAlive(x, y+1) ? 1 : 0;
        num += isAlive(x+1, y-1) ? 1 : 0;
        num += isAlive(x+1, y) ? 1 : 0;
        num += isAlive(x+1, y+1) ? 1 : 0;
        return num;
    }

    public void randomize() {
        Random rand = new Random();
        for(int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                gridlife[i][j] = rand.nextInt(2) == 1;
            }
        }
    }

    public void initIHeptomino() {
        int startx = (int) (double) (width / 2);
        int starty = (int) (double) (height / 2);
        gridlife[startx][starty] = true;
        gridlife[startx+1][starty] = true;
        gridlife[startx+1][starty+1] = true;
        gridlife[startx+1][starty+2] = true;
        gridlife[startx+2][starty+2] = true;
        gridlife[startx+2][starty+3] = true;
        gridlife[startx+3][starty+3] = true;


    }

}
