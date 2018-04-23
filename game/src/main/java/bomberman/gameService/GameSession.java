package bomberman.gameService;

import bomberman.model.Character;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;


public class GameSession implements Runnable{

    private Long gameId;
    private BlockingQueue inputQueue;
    private int numOfPlayers;
    private Character[] charList;
    private int connectionsNum;
    private ConnectionPool pool;

    public boolean isReady(){
        return numOfPlayers == connectionsNum;
    }

    GameSession(long id, BlockingQueue queue,int numberOfPlayers){
        this.inputQueue = queue;
        this.gameId = id;
        this.numOfPlayers = numberOfPlayers;
        this.charList = new Character[numberOfPlayers];
        this.connectionsNum = 0;
        this.pool = new ConnectionPool();
    }

    public int addCharacter(WebSocketSession session,String owner){
        pool.setSession(session);
        switch (connectionsNum){
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
        }
        return connectionsNum;
    }

    public void run(){
        gameLoop();
    }

    private void gameLoop(){
        while (!Thread.currentThread().isInterrupted()){

        }
    }

}
