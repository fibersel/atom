package matchmaker;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by imakarycheva on 04.04.18.
 */
public class RepositoryTest {

    private static JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void setUp() {
        DataSource db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
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
        Integer userCount = jdbcTemplate.query("SELECT count(*) as count FROM mm.users WHERE login = ?", param,
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
        jdbcTemplate.update("INSERT INTO mm.users (login, rank) VALUES (?,?)", params);
        int gotRank = repository.getUserRank(existentLogin);
        Assert.assertTrue(expectedRank == gotRank);
    }

    @Test
    public void saveSessionTest() {
        MatchMakerRepository repository = new MatchMakerRepository(jdbcTemplate);
        String[] logins = {"saveSessionTest1", "saveSessionTest2", "saveSessionTest3"};
        long sessionId = 404L;

        for (String login : logins) {
            repository.saveLogin(login);
        }

        repository.saveGameSession(sessionId, logins);

        Object[] param = {sessionId};
        Integer sessionCount = jdbcTemplate.query("SELECT count(*) as count FROM mm.game_sessions WHERE id = ?",
                param, (rs, num) -> rs.getInt("count"))
                .get(0);
        Assert.assertTrue(sessionCount.equals(1));

        List<String> gotLogins = jdbcTemplate.query("SELECT login FROM mm.users t " +
                        "JOIN mm.game_sessions_to_users t2 ON t.id = t2.user_id WHERE t2.game_session_id = ? " +
                        "ORDER BY t.login", param, (rs, num) -> rs.getString("login"));
        Assert.assertEquals(logins.length, gotLogins.size());
        for (int i = 0; i < logins.length; i++) {
            Assert.assertEquals(logins[i], gotLogins.get(i));
        }
    }
}
