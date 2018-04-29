package bomberman.model;

import bomberman.gameservice.GameSession;
import bomberman.model.geometry.Bar;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Character {
    private final String type = "Pawn";
    private final int id;
    private Point position;
    private Direction direction;
    private boolean alive;


    @JsonIgnore
    private String owner;
    @JsonIgnore
    private DataContainer container;
    @JsonIgnore
    private List<Bar> bars;
    @JsonIgnore
    private Bar mainBer;
    @JsonIgnore
    private long velocity = 1;
    @JsonIgnore
    private int bombsCtr = 1;
    public static int size = 28;


    public Character(int x, int y, String owner, int id,DataContainer container) {
        this.position = new Point(x, y);
        this.alive = true;
        this.owner = owner;
        this.id = id;
        this.direction = Direction.DOWN;
        this.container = container;
        bars = getBarsByPosition(position);
    }

    public void plant(){
        if(bombsCtr-- > 0 && mainBer.isFree()) {
            Bomb bomb = new Bomb(GameSession.id++, mainBer, 1,this);
            container.getObjsToTick().add(bomb);
            container.getObjsToSend().add(bomb);
        }
    }

    public void move(String direction,long frametime){
        long distance = frametime * velocity;
        long allowed;
        int delta;
        int X = position.getX() / Bar.getSize();
        int Y = position.getY() / Bar.getSize();
        System.out.println(direction);
        switch (direction){
            case "{\"direction\":\"UP\"}":
                this.direction = Direction.UP;
                allowed = position.getX() % Bar.getSize();
                for (int i = X;container.getField().getBar(i,Y).isFree();i--)
                    allowed += Bar.getSize();
                delta = (int)Math.min(allowed,distance);
                position.setX(position.getX() - delta);
                break;
            case "{\"direction\":\"DOWN\"}":
                this.direction = Direction.DOWN;
                allowed = Bar.getSize() - position.getX() % Bar.getSize() - size;
                for (int i = X;container.getField().getBar(X,i).isFree();i++)
                    allowed += Bar.getSize();
                delta = (int)Math.min(allowed,distance);
                position.setX(position.getX() + delta);
                break;
            case "{\"direction\":\"LEFT\"}":
                this.direction = Direction.LEFT;
                allowed = position.getY() % Bar.getSize();
                for (int i = Y;container.getField().getBar(X,i).isFree();i--)
                    allowed += Bar.getSize();
                delta = (int)Math.min(allowed,distance);
                position.setY(position.getY() - delta);
                break;
            case "{\"direction\":\"RIGHT\"}":
                this.direction = Direction.RIGHT;
                allowed = Bar.getSize() -  (position.getX() + size) % Bar.getSize();
                for (int i = position.getX() + size > Bar.getSize() ? X + 1 : X;container.getField().getBar(i,Y).isFree();i++) {
                    System.out.println(i + "  " + Y);
                    allowed += Bar.getSize();
                }
                delta = (int)Math.min(allowed,distance);
                position.setX(position.getX() + delta);
                break;
        }
        bars.stream().forEach(e->e.removeChar(this));
        bars = getBarsByPosition(position);
    }

    public void addBomb(){
        bombsCtr++;
    }

    public String getType() {
        return type;
    }

    public List<Bar> getBarsByPosition(Point position){
        List<Bar> tmp = new LinkedList<>();
        int x = position.getX() / Bar.getSize();
        int y = position.getY() / Bar.getSize();
        System.out.println(x);
        System.out.println(y);
        tmp.add(container.getField().getBar(x,y));
        if(position.getX() % 48 + Character.size > Bar.getSize())
            tmp.add(container.getField().getBar(x + 1,y));
        else  if (position.getY() % 48 + Character.size > Bar.getSize())
            tmp.add(container.getField().getBar(x,y + 1));
        tmp.forEach(e->e.addChar(this));
        return tmp;
    }


    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void kill(){
        alive = false;
        container.getObjsToSend().remove(this);
    }

}
