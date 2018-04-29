package bomberman.model.geometry;

import bomberman.model.Wood;
import bomberman.model.Wall;
import bomberman.model.Bomb;
import bomberman.model.Character;
import bomberman.model.Pathless;
import bomberman.model.Point;

import java.util.LinkedList;
import java.util.List;

public class Bar implements Collider {
    private int coordX;
    private int coordY;
    private Point position;
    private Pathless plug = null;
    private List<Character> chars;
    private static int size = 32;

    public Bar(int coordX,int coordY) {
        this.position = new Point(coordX,coordY);
        this.coordX = coordX / Bar.size;
        this.coordY = coordY / Bar.size;
        this.chars = new LinkedList<>();
    }

    @Override
    public boolean isColliding(Collider other) {
        return false;
    }

    public static int getSize() {
        return size;
    }

    public void setBomb(Bomb bomb) {
        if (plug != null) {
            plug = bomb;
            bomb.setBar(this);
        }
    }


    public boolean isFree() {
        return plug == null;
    }


    public void blowBomb() {
        Bomb bomb = (Bomb)plug;
        chars.stream().forEach(Character::kill);
        chars = new LinkedList<>();
        for (int i = 0;i < bomb.getStrength();i++)
            plug = null;
    }

    public boolean isWall() {
        if (plug == null) {
            return false;
        }
        return plug.getClass() == Wall.class || plug.getClass() == Wood.class;
    }

    public void setWall(Wall wall) {
        if (wall != null) {
            plug = wall;
        }
    }

    public void setWood(Wood wood) {
        if (wood != null) {
            plug = wood;
        }
    }

    public Pathless getWood() {
        return plug;
    }

    public void removeWood() {
        plug = null;
    }

    public Point getPosition() {
        return position;
    }

    public void addChar(Character c) {
        chars.add(c);
    }


    public void removeChar(Character c) {
        chars.remove(c);
    }
}
