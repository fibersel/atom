package model;

/**
 * Created by imakarycheva on 22.04.18.
 */
public class Bonus implements Tickable {
    private final String type = "Bonus";
    private final int id;
    private final Point position;
    private final BonusType bonusType;

    public Bonus(int id, int x, int y, BonusType bonusType) {
        this.id = id;
        this.bonusType = bonusType;
        position = new Point(x, y);
    }

    public int getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    @Override
    public void tick(long elapsed) {
        
    }
}
