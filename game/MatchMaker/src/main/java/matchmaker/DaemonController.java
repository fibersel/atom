package matchmaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.context.ApplicationContext;
import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Controller
public class DaemonController {
    ArrayList<Thread> daemons;

    @Autowired
    ApplicationContext context;

    @PostConstruct
    public void activation(){
        daemons = new ArrayList<Thread>(0);
        for (int i = 0; i < MatchMaker.RANK_NUMBER; i++) {
            MatchMakerDaemon d = (MatchMakerDaemon) context.getBean(MatchMakerDaemon.class);
            d.setQueueNumber(i);
            daemons.add(new Thread(d));
        }
        for (Thread e: daemons) {
            e.start();
        }
    }
}
