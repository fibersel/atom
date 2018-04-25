package bomberman.gameservice;

import bomberman.model.Bomb;
import bomberman.model.Character;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;


public class GameSession implements Runnable {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameSession.class);
    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;
    private long tickNumber = 0;

    private Long gameId;
    private BlockingQueue inputQueue;
    private int numOfPlayers;
    private Character[] charList;
    private int connectionsNum;
    private ConnectionPool pool;
    private Collection<Bomb> bombs;

    public boolean isReady() {
        return numOfPlayers == connectionsNum;
    }

    GameSession(long id, BlockingQueue queue,int numberOfPlayers) {
        this.inputQueue = queue;
        this.gameId = id;
        this.numOfPlayers = numberOfPlayers;
        this.charList = new Character[numberOfPlayers];
        this.connectionsNum = 0;
        this.pool = new ConnectionPool();
        this.bombs = new LinkedList<>();
    }

    public int addCharacter(WebSocketSession session,String owner) {
        pool.setSession(session);
        switch (connectionsNum) {
            case 0:
                charList[connectionsNum++] = new Character(10,10,owner);
                break;
            case 1:
                charList[connectionsNum++] = new Character(750,10,owner);
                break;
            case 2:
                charList[connectionsNum++] = new Character(750,550,owner);
                break;
            case 3:
                charList[connectionsNum++] = new Character(710,550,owner);
                break;
            default:
                break;
        }
        return connectionsNum - 1;
    }

    public void run() {
        gameLoop();
    }

    private void gameLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            long started = System.currentTimeMillis();
            act(FRAME_TIME);
            long elapsed = System.currentTimeMillis() - started;
            if (elapsed < FRAME_TIME) {
                log.info("All tick finish at {} ms", elapsed);
            } else
                log.info("{}: tick ", tickNumber);
            tickNumber++;
        }
    }


    private void act(long elapsed){

    }

}
