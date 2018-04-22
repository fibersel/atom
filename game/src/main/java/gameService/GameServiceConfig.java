package gameService;

import model.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class GameServiceConfig {

    @Bean(name = "queues")
    public ConcurrentHashMap<Long,BlockingQueue<Message>> getGamesQueueMap(){
        return new ConcurrentHashMap<>();
    }

    @Bean(name = "games")
    public ConcurrentHashMap<Long,GameSession> getGamesMap(){
        return new ConcurrentHashMap<>();
    }
}
