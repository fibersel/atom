package bomberman.model;

public class Character implements Tickable {
    private Point position;
    private int speed;
    private int range;
    private boolean alive;
    private final String type = "Pawn";
    private String owner;
    private Direction direction;

    public Character(int x, int y, String owner) {
        this.position = new Point(x, y);
        this.alive = true;
        this.owner = owner;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public void setX(int x) {
        this.position.setX(x);
    }

    public void setY(int y) {
        this.position.setY(y);
    }

    public void kill() {
        this.alive = false;
    }

    public Point getPosition() {
        return position;
    }

    @Override
    public void tick(long elapsed) {

    }

    public String getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
