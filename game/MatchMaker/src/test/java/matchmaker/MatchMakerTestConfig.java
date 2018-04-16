package matchmaker;

import okhttp3.OkHttpClient;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@TestConfiguration
public class MatchMakerTestConfig {

    @Bean
    public ConcurrentHashMap<Long,Integer> returnedRequests(){ return new ConcurrentHashMap<>(); }

    @Bean
    public ConcurrentHashMap<String,Long> getPlayersMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(){
        final String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
        final String username = "postgres";
        final String password = "Makar1988";
        return new JdbcTemplate(DataSourceBuilder.create().url(jdbcUrl).username(username).password(password).build());
    }

    @Bean
    public MatchMakerRepository getRepository() {
        return new MatchMakerRepository(getJdbcTemplate());
    }

    @Bean
    @Scope("prototype")
    public BlockingQueue<String> getBlockingQueue(){ return new LinkedBlockingQueue<String>(); }

    @Bean
    @Scope("prototype")
    public MatchMakerDaemon getDaemon() {
        return new MatchMakerDaemon();
    }

    @Bean
    public OkHttpClient getClient() {
        return new OkHttpClient();
    }

    @Bean
    @Scope("prototype")
    public TestClient getTestClient() {
        return new TestClient();
    }

}
