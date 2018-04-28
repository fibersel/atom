package bomberman.gameservice;

import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConnectionPool {

    Set<WebSocketSession> sessions = new HashSet<>();

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    public void setSession(WebSocketSession session) {
        this.sessions.add(session);
    }

    public void broadcast(String msg) throws IOException {
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(msg));
        }
        log.info("Sent message: " + msg);
    }
}