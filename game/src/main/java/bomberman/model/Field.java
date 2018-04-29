package bomberman.model;

import bomberman.gameservice.GameSession;
import bomberman.model.geometry.Bar;

import java.util.LinkedList;
import java.util.List;

public class Field {

    private Bar field[][];

    private int height = 13;
    private int width = 17;
    private List<Object> objects = new LinkedList<>();

    public Field(){
        field = new Bar[height][width];
        for(int i = 0;i < height;i++)
            for (int j = 0;j < width;j++){
                field[i][j] = new Bar(j * Bar.getSize(),i * Bar.getSize());
                if(i == 0 || i == height - 1 || j == 0 || j == width - 1){
                    Wall wall = new Wall(GameSession.id++,field[i][j]);
                    field[i][j].setWall(wall);
                    objects.add(wall);
                } else if(j % 2 == 1 || i % 2 == 1) {
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

    public List getObjects(){
        return objects;
    }

    public Bar getBar(int x, int y){
        return field[y][x];
    }
}
