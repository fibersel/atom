package bomberman.model;

import bomberman.model.geometry.Bar;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by imakarycheva on 22.04.18.
 */
public class Bomb implements Tickable,Pathless {
    private final String type = "Bomb";
    private int id;
    private Point position;
    private int strength;

    private static int size = 28;
    @JsonIgnore
    private Bar bar;
    @JsonIgnore
    private long timer = 0;
    @JsonIgnore
    private Character owner;

    public Bomb(int id,Bar bar, int strength,Character owner) {
        this.id = id;
        this.strength = strength;
        this.bar = bar;
        bar.setBomb(this);
        this.position = bar.getPosition();
        this.owner = owner;
    }




    @Override
    public void tick(long elapsed) {
        if(timer > 3000)
            blow();
        else timer += elapsed;
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

    public void blow(){
        bar.blowBomb();
        owner.addBomb();
    }
}
