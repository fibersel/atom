package model;

public class Character implements Tickable{
    private int x;
    private int y;
    private int speed;
    private int range;
    private boolean alive;

    Character(int x,int y){
        this.x = x;
        this.y = y;
        this.alive = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void kill(){
        this.alive = false;
    }

    @Override
    public void tick(long elapsed) {

    }
}
