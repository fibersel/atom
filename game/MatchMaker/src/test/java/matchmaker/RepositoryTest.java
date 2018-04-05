package matchmaker;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Created by imakarycheva on 04.04.18.
 */
public class RepositoryTest {

    private static JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setUp() {
        DataSource db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("data.sql")
                .build();
        jdbcTemplate = new JdbcTemplate(db);
    }

    @Test
    public void getRankForNewUserTest() {
        MatchMakerRepository repository = new MatchMakerRepository(jdbcTemplate);
        String newLogin = "NEW_USER";
        int rank = repository.getUserRank(newLogin);
        Assert.assertTrue(rank == 0);
        Object[] param = {newLogin};
        Integer userCount = jdbcTemplate.query("SELECT count(*) as count FROM users WHERE login = ?", param,
                (rs, num) -> rs.getInt("count"))
                .get(0);
        Assert.assertTrue(userCount.equals(1));
    }

    @Test
    public void getRankForExistentUserTest() {
        MatchMakerRepository repository = new MatchMakerRepository(jdbcTemplate);
        String existentLogin = "EXISTENT_USER";
        int expectedRank = 100;
        Object[] params = {existentLogin, expectedRank};
        jdbcTemplate.update("INSERT INTO users (login, rank) VALUES (?,?)", params);
        int gotRank = repository.getUserRank(existentLogin);
        Assert.assertTrue(expectedRank == gotRank);
    }

    @Test
    public void saveSessionTest() {
        MatchMakerRepository repository = new MatchMakerRepository(jdbcTemplate);
        String[] logins = {"login1", "login2", "login3"};
        long sessionId = 404L;

        for (String login : logins) {
            repository.saveLogin(login);
        }

        repository.saveGameSession(sessionId, logins);

        Object[] param = {sessionId};
        Integer sessionCount = jdbcTemplate.query("SELECT count(*) as count FROM game_sessions WHERE id = ?",
                param, (rs, num) -> rs.getInt("count"))
                .get(0);
        Assert.assertTrue(sessionCount.equals(1));

        for (String login : logins) {
            Object[] parameter = {login};
            Integer loginCount = jdbcTemplate.query("SELECT count(*) as count FROM game_sessions_to_users t " +
                            "JOIN users u on t.user_id = u.id WHERE u.login = ?",
                    parameter, (rs, num) -> rs.getInt("count"))
                    .get(0);
            Assert.assertTrue(loginCount.equals(1));
        }
    }
}
