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
}
