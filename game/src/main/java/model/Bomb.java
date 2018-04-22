package model;

/**
 * Created by imakarycheva on 22.04.18.
 */
public class Bomb implements Tickable {
    private final String type = "Bomb";
    private int id;
    private Point position;
    private int strength;

    public Bomb(int id, int x, int y, int strength) {
        this.id = id;
        this.strength = strength;
        position = new Point(x, y);
    }




    @Override
    public void tick(long elapsed) {

    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public int getStrength() {
        return strength;
    }
}
