package bomberman.model;

/**
 * Created by imakarychev on 22.04.18.
 */
public class Wall implements Tickable {
    private final String type = "Wall";
    private int id;
    private Point position;

    public Wall(int id, int x, int y) {
        this.id = id;
        this.position = new Point(x, y);
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
}
