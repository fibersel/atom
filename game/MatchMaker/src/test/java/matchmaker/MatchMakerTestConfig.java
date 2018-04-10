package matchmaker;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MatchMakerTestConfig {

    @Bean
    public Hashtable<Long,Integer> returnedRequests(){ return new Hashtable<>(); }

    @Bean
    public JdbcTemplate getJdbcTemplate(){
        final String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
        final String username = "svasilyev";
        final String password = "1111";
        return new JdbcTemplate(DataSourceBuilder.create().url(jdbcUrl).username(username).password(password).build());
    }

}
