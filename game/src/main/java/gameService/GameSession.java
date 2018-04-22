package gameService;


import java.util.concurrent.BlockingQueue;


public class GameSession implements Runnable{

    private Long gameId;

    private int numOfPlayers;

    private Character[] CharList;

    private BlockingQueue inputQueue;

    public int connections;

    public boolean isReady(){
        return numOfPlayers == connections;
    }

    GameSession(long id, BlockingQueue queue,int numberOfPlayers){
        this.inputQueue = queue;
        this.gameId = id;
        this.numOfPlayers = numberOfPlayers;
        this.CharList = new Character[numberOfPlayers];
    }

    public void run(){
        gameLoop();
    }

    private void gameLoop(){
        while (!Thread.currentThread().isInterrupted()){

        }
    }

}
