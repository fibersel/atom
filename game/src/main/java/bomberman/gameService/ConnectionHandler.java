package bomberman.gameService;

import bomberman.model.Message;
import bomberman.util.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConnectionHandler extends TextWebSocketHandler implements WebSocketHandler {

    private static Pattern gameId = Pattern.compile("gameId=(\\d)&");
    private static Pattern name = Pattern.compile("name=(.+)");

    @Autowired
    private ApplicationContext ctx;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Matcher idMatcher = gameId.matcher(session.getUri().getQuery());
        Matcher nameMatcher = name.matcher(session.getUri().getQuery());
        Long gameId;
        int playerId;
        String name;
        idMatcher.find();
        nameMatcher.find();
        gameId = Long.parseLong(idMatcher.group(1));
        name = nameMatcher.group(1);
        session.getAttributes().put("msgQueue",ctx.getBean(GameService.class).getQueue(gameId));
        playerId = ctx.getBean(GameService.class).addPlayer(gameId,session,name);
        session.getAttributes().put("playerId",playerId);
        System.out.println(gameId + "    " + name);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Message msg = JsonHelper.fromJson(message.getPayload(),Message.class).setPlayerId(
                (int)session.getAttributes().get("playerId"));
        BlockingQueue<Message> queue = (BlockingQueue<Message>)session.getAttributes().get("msgQueue");
        queue.put(msg);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }
}
