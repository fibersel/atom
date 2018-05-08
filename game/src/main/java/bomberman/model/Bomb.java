package bomberman.model;

import bomberman.gameservice.GameSession;
import bomberman.model.geometry.Bar;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by imakarycheva on 22.04.18.
 */
public class Bomb implements Tickable {
    private final String type = "Bomb";
    private int id;
    private Point position;
    private int strength;

    private static int size = 28;

    @JsonIgnore
    private final long cooldown= 5000;
    @JsonIgnore
    private Bar bar;
    @JsonIgnore
    private long timer;
    @JsonIgnore
    private Character owner;

    public Bomb(int id,Bar bar, int strength,Character owner) {
        this.id = id;
        this.strength = strength;
        this.bar = bar;
        this.bar.setBomb(this);
        this.position = bar.getPosition();
        this.owner = owner;
        this.timer = System.currentTimeMillis();
    }

    @Override
    public void tick(long elapsed) {
        if (System.currentTimeMillis() - timer > cooldown) {
            blow();
        }
    }

    public void dropCooldown() {
        timer -= cooldown;
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

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public int getSize() {
        return size;
    }

    public void blow() {
        timer = Long.MAX_VALUE;
        bar.removeBomb();
        ArrayList<Bar> temp = owner.getContainer().getField().getBarsInRadius(strength, bar.getCoordX(), bar.getCoordY());
        for (Bar b: temp) {
            if (b.isWood()) {
                if (!owner.getContainer().getObjsToSend().contains(b.getPlug())) {
                    owner.getContainer().getObjsToSend().add(b.getPlug());
                }
            }
            if (b.bombStands()) {
                b.getBomb().dropCooldown();
            }
            Fire fire = new Fire(GameSession.id++, b);
            owner.getContainer().getObjsToSend().add(fire);
            b.removeWood();
            b.getChars().stream().forEach(Character::kill);
        }
        owner.getContainer().getObjsToSend().remove(this);
        owner.getContainer().getObjsToTick().remove(this);
        owner.addBomb();
    }
}
