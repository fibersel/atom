package bomberman.gameservice;


import bomberman.model.*;
import bomberman.model.Character;
import bomberman.util.JsonHelper;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;


public class GameSession implements Runnable {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameSession.class);
    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;
    private long tickNumber = 0;
    public static int id = 0;
    private Long gameId;
    private BlockingQueue<Message> inputQueue;
    private Queue<Message> buffer;
    private int numOfPlayers;
    private HashMap<Integer, Character> charList;
    private int connectionsNum;
    private ConnectionPool pool;
    private DataContainer container;

    public boolean isReady() {
        return numOfPlayers == connectionsNum;
    }

    public DataContainer getContainer() {
        return container;
    }

    public GameSession(long id, BlockingQueue queue, int numberOfPlayers) {
        this.inputQueue = queue;
        this.gameId = id;
        this.numOfPlayers = numberOfPlayers;
        this.charList = new HashMap<>();
        this.connectionsNum = 0;
        this.pool = new ConnectionPool();
        this.buffer = new LinkedList<>();
        this.container = new DataContainer();
        container.setObjsToSend(container.getField().getObjects());
    }

    public synchronized int addCharacter(WebSocketSession session, String owner) {
        pool.setSession(session);
        Integer newId = id++;
        switch (connectionsNum++) {
            case 0:
                charList.put(newId, new Character(32, 32, owner, id++, container));
                cornerLd();
                break;
            case 1:
                charList.put(newId, new Character(480, 32, owner, id++, container));
                cornerRd();
                break;
            case 2:
                charList.put(newId, new Character(480, 352, owner, id++, container));
                cornerRu();
                break;
            case 3:
                charList.put(newId, new Character(32, 352, owner, id++, container));
                cornerLu();
                break;
            default:
                break;
        }
        container.getObjsToSend().add(charList.get(newId));

        if (isReady()) {
            new Thread(this).start();
            log.info("Game # {} started", gameId);
        }
        return newId;
    }

    public void run() {
        try {
            Message replicaMsg = new Message(Topic.REPLICA, JsonHelper.toJson(container.getObjsToSend()));
            pool.broadcast(JsonHelper.toJson(replicaMsg));
        } catch (IOException e) {
            log.error(e.getMessage(), e.getStackTrace());
        }
        container.getObjsToSend().clear();
        for (Character c: charList.values())
            container.getObjsToSend().add(c);
        gameLoop();
    }

    private void gameLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            long started = System.currentTimeMillis();
            act(FRAME_TIME);

            try {
                Message replicaMsg = new Message(Topic.REPLICA, JsonHelper.toJson(container.getObjsToSend()));
                pool.broadcast(JsonHelper.toJson(replicaMsg));

                for (Object o: container.getObjsToSend()) {
                    if (o.getClass() == Character.class) {
                        Character character = (Character) o;
                        if (!character.isAlive()) {
                            container.getObjsToSend().remove(o);
                        }
                    }
                    if (o.getClass() == Wood.class) {
                        container.getObjsToSend().remove(o);
                    }
                    if (o.getClass() == Fire.class) {
                        container.getObjsToSend().remove(o);
                    }
                }

                for (Integer key: charList.keySet())
                    charList.get(key).setDirection(Direction.DEFAULT);
            } catch (IOException e) {
                log.error(e.getMessage(), e.getStackTrace());
                tickNumber++;
                continue;
            }



            long elapsed = System.currentTimeMillis() - started;
            if (elapsed < FRAME_TIME) {
                log.info("All tick finish at {} ms", elapsed);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(FRAME_TIME - elapsed));
            } else {
                log.warn("tick lag {} ms", elapsed - FRAME_TIME);
            }
            tickNumber++;
        }
    }


    private void act(long frametime) {
        synchronized (inputQueue) {
            while (!inputQueue.isEmpty())
                buffer.add(inputQueue.poll());
        }
        Message tmp;
        while (!buffer.isEmpty()) {
            tmp = buffer.poll();
            if (tmp.getTopic() == Topic.PLANT_BOMB)
                charList.get(tmp.getPlayerId()).plant();
            else
                charList.get(tmp.getPlayerId()).move(tmp.getData(), frametime);
        }
        container.getObjsToTick().stream().forEach(e -> e.tick(frametime));
    }


    public void cornerLd() {
        container.getObjsToSend().remove(container.getField().getBar(1, 1).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(1, 2).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(2, 1).getPlug());
        container.getField().getBar(1, 1).clearBar();
        container.getField().getBar(1, 2).clearBar();
        container.getField().getBar(2, 1).clearBar();
    }


    public void cornerRd() {
        container.getObjsToSend().remove(container.getField().getBar(15, 1).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(15, 2).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(14, 1).getPlug());
        container.getField().getBar(15, 1).clearBar();
        container.getField().getBar(15, 2).clearBar();
        container.getField().getBar(14, 1).clearBar();


    }

    private void cornerRu() {
        container.getObjsToSend().remove(container.getField().getBar(15, 11).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(15, 10).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(14, 11).getPlug());
        container.getField().getBar(15, 11).clearBar();
        container.getField().getBar(15, 10).clearBar();
        container.getField().getBar(14, 11).clearBar();
    }


    private void cornerLu() {
        container.getObjsToSend().remove(container.getField().getBar(1, 11).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(1, 10).getPlug());
        container.getObjsToSend().remove(container.getField().getBar(2, 11).getPlug());
        container.getField().getBar(1, 11).clearBar();
        container.getField().getBar(1, 10).clearBar();
        container.getField().getBar(2, 11).clearBar();
    }

}
