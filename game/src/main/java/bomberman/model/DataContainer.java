package bomberman.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DataContainer {
    private Set<Tickable> objsToTick;
    private List objsToSend;
    private Field field;

    public DataContainer() {
        this.objsToTick = new HashSet<>();
        this.objsToSend = new LinkedList();
        this.field = new Field();
        for (Object o: field.getObjects())
            objsToSend.add(o);
    }


    public Field getField() {
        return field;
    }

    public void setObjsToSend(List list) {
        objsToSend = list;
    }

    public List getObjsToSend() {
        return objsToSend;
    }

    public Set<Tickable> getObjsToTick() {
        return objsToTick;
    }
}
