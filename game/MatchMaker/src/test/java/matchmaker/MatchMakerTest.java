package matchmaker;

import org.junit.Test;


public class MatchMakerTest {


    @Test
    public void matchMakerTest(){
        for (int i = 0;i < 4;i++) {
            Thread thread = new Thread(new TestClient());
            thread.run();
        }
    }

}
