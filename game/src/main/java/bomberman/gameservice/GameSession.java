package bomberman.gameservice;

import bomberman.model.*;
import bomberman.model.Character;
import bomberman.util.JsonHelper;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;


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
                RMLDcorner();
                break;
            case 1:
                charList.put(newId, new Character(480, 32, owner, id++, container));
                RMRDcorner();
                break;
            case 2:
                charList.put(newId, new Character(480, 352, owner, id++, container));
                RMRUcorner();
                break;
            case 3:
                charList.put(newId, new Character(32, 352, owner, id++, container));
                RMLUcorner();
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

        gameLoop();
    }

    private void gameLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            long started = System.currentTimeMillis();
            act(FRAME_TIME);
            long elapsed = System.currentTimeMillis() - started;


            try {
                Message replicaMsg = new Message(Topic.REPLICA, JsonHelper.toJson(container.getObjsToSend()));
                pool.broadcast(JsonHelper.toJson(replicaMsg));
                for(Integer key: charList.keySet())
                    charList.get(key).setDirection(Direction.DOWN);
            } catch (IOException e) {
                log.error(e.getMessage(), e.getStackTrace());
                tickNumber++;
                continue;
            }

            /*
            if (elapsed < FRAME_TIME) {
                log.info("All tick finish at {} ms", elapsed);
            } else
                log.info("{}: tick ", tickNumber);
            */
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


    private void RMLDcorner() {
        container.getObjsToSend().remove(container.getField().getBar(1, 1).getWood());
        container.getObjsToSend().remove(container.getField().getBar(1, 2).getWood());
        container.getObjsToSend().remove(container.getField().getBar(2, 1).getWood());
        container.getField().getBar(1, 1).removeWood();
        container.getField().getBar(1, 2).removeWood();
        container.getField().getBar(2, 1).removeWood();
    }


    private void RMRDcorner() {
        container.getObjsToSend().remove(container.getField().getBar(15, 1).getWood());
        container.getObjsToSend().remove(container.getField().getBar(15, 2).getWood());
        container.getObjsToSend().remove(container.getField().getBar(14, 1).getWood());
        container.getField().getBar(15, 1).removeWood();
        container.getField().getBar(15, 2).removeWood();
        container.getField().getBar(14, 1).removeWood();


    }

    private void RMRUcorner() {
        container.getObjsToSend().remove(container.getField().getBar(15, 11).getWood());
        container.getObjsToSend().remove(container.getField().getBar(15, 10).getWood());
        container.getObjsToSend().remove(container.getField().getBar(14, 11).getWood());
        container.getField().getBar(15, 11).removeWood();
        container.getField().getBar(15, 10).removeWood();
        container.getField().getBar(14, 11).removeWood();
    }


    private void RMLUcorner() {
        container.getObjsToSend().remove(container.getField().getBar(1, 11).getWood());
        container.getObjsToSend().remove(container.getField().getBar(1, 10).getWood());
        container.getObjsToSend().remove(container.getField().getBar(2, 11).getWood());
        container.getField().getBar(1, 11).removeWood();
        container.getField().getBar(1, 10).removeWood();
        container.getField().getBar(2, 11).removeWood();
    }
}
