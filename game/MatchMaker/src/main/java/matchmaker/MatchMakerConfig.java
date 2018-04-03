package matchmaker;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class MatchMakerConfig {
    @Bean
    public ArrayList<LinkedBlockingQueue<String>> getQueues () {
        ArrayList<LinkedBlockingQueue<String>> temp = new ArrayList<LinkedBlockingQueue<String>>(0);
        for (int i = 0; i < MatchMaker.RANK_NUMBER; i++) {
            temp.add(new LinkedBlockingQueue<String>());
        }
        return temp;
    }


    /*
    @Bean
    public BlockingQueue<String> getBlockingQueue(){ return new LinkedBlockingQueue<String>(); }
    */

    @Bean
    public OkHttpClient getClient(){ return new OkHttpClient(); }

    @Bean
    public ConcurrentHashMap<String,Long> getConcurrentHashMap(){ return new ConcurrentHashMap<String,Long>(); }
}
