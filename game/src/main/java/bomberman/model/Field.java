package bomberman.model;

import bomberman.gameservice.GameSession;
import bomberman.model.geometry.Bar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Field {
    public static int height = 13;
    public static int width = 17;

    private Bar[][] field;
    private CopyOnWriteArrayList<Object> objects = new CopyOnWriteArrayList<>();

    public Field() {
        field = new Bar[height][width];
        for (int i = 0;i < height;i++)
            for (int j = 0;j < width;j++) {
                field[i][j] = new Bar(j * Bar.getSize(),i * Bar.getSize(), j, i);
                if (i == 0 || i == height - 1 || j == 0 || j == width - 1) {
                    Wall wall = new Wall(GameSession.id++,field[i][j]);
                    field[i][j].setWall(wall);
                    objects.add(wall);
                } else if (j % 2 == 1 || i % 2 == 1) {
                    Wood wood = new Wood(GameSession.id++,field[i][j]);
                    field[i][j].setWood(wood);
                    objects.add(wood);
                } else {
                    Wall wall = new Wall(GameSession.id++,field[i][j]);
                    field[i][j].setWall(wall);
                    objects.add(wall);
                }
            }
    }

    public CopyOnWriteArrayList<Object> getObjects() {
        return objects;
    }

    public Bar getBar(int x, int y) {
        return field[y][x];
    }

    ArrayList<Bar> getBarsAround(int centerX, int centerY) {
        ArrayList<Bar> bars = new ArrayList<>();
        if ((centerX >= 0) && (centerX < width) && (centerY >= 0) && (centerY < height))
            bars.add(getBar(centerX, centerY));
        if ((centerX - 1 >= 0) && (centerX - 1 < width) && (centerY >= 0) && (centerY < height))
            bars.add(getBar(centerX - 1, centerY));
        if ((centerX >= 0) && (centerX < width) && (centerY - 1 >= 0) && (centerY - 1 < height))
            bars.add(getBar(centerX, centerY - 1));
        if ((centerX + 1 >= 0) && (centerX + 1 < width) && (centerY >= 0) && (centerY < height))
            bars.add(getBar(centerX + 1, centerY));
        if ((centerX >= 0) && (centerX < width) && (centerY + 1 >= 0) && (centerY + 1 < height))
            bars.add(getBar(centerX, centerY + 1));

        return bars;
    }
}
