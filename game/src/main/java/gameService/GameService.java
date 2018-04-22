package gameService;

import matchmaker.MatchMakerRepository;
import model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Controller
@RequestMapping("game")
public class GameService {

    @Autowired
    MatchMakerRepository repository;

    @Resource(name = "games")
    private ConcurrentHashMap<Long,GameSession> games;

    @Resource(name = "queues")
    private ConcurrentHashMap<Long,BlockingQueue<Message>> gameQueues;

    private static volatile Long numOfGame = 0L;

    @PostConstruct
    private void init() {
        numOfGame = repository.getLastSessionId() + 1;
    }


    /*
     *  curl -X POST -i http://localhost:8080/game/create -d "playerCount=4"
     * */

    @RequestMapping(
            path = "create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity create(@RequestParam("playerCount") int playerCount) {
        Long id = ++numOfGame;
        BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        GameSession session = new GameSession(id,queue,playerCount);
        gameQueues.put(id,queue);
        games.put(id,session);
        return ResponseEntity.ok(numOfGame.toString());
    }

}
