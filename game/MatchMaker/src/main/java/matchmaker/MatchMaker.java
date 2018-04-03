package matchmaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("matchmaker")
public class MatchMaker {
    /*
    @Autowired
    private BlockingQueue<String> playersQueue;
    */

    @Autowired
    private ArrayList<BlockingQueue<String>> playersQueues;

    @Autowired
    private ConcurrentHashMap<String,Long> playersId;

    @Autowired
    private MatchMakerRepository repository;

    private static boolean enabled = false;

    private static final int[] RANK_BORDERS = {10, 20, 30 ,40, 50};
    public static final int RANK_NUMBER = 5;

    /*
     *   curl -X POST -i http://localhost:8080/matchmaker/join -d "name=test"
     * */

    @RequestMapping(
            path = "join",
            method = RequestMethod.POST,
            consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity join(@RequestParam("name") String name) throws InterruptedException{
        int rank = repository.getUserRank(name);
        for (int i = 0; i < RANK_NUMBER; i++) {
            if (rank < RANK_BORDERS[i]) {
                playersQueues.get(i).offer(name);
                break;
            }
        }
        while (!playersId.containsKey(name))
            Thread.sleep(10);
        Long id = playersId.get(name);
        return ResponseEntity.ok(id);
    }


}
