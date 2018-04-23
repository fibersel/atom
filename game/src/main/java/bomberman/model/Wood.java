package bomberman.model;

/**
 * Created by imakarycheva on 22.04.18.
 */
public class Wood implements Tickable {
    private final String type = "Wood";
    private final int id;
    private final Point position;

    public Wood(int id, int x, int y) {
        this.id = id;
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
}
