package ru.atom.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.*;
import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Controller
@RequestMapping("chat")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private HistoryFile historyFile;
    private Queue<String> messages = new ConcurrentLinkedQueue<>();
    private Map<String, String> usersOnline = new ConcurrentHashMap<>();
    private Pattern pattern = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\." +
            "[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)");
    private Date date;
    private SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss");
    private Map<String,UserMetadata> antiSpamArchive = new HashMap<>();
    /**
     * curl -X POST -i localhost:8080/chat/login -d "name=I_AM_STUPID"
     */

    @PostConstruct
    public void processing() {
        File file = historyFile.getHistoryFile();
        Scanner reader;
        try {
            reader = new Scanner(file);
            while (reader.hasNext())
                messages.add(reader.nextLine());
        } catch (Exception e){
            log.info("Fail!");
        }

    }

    @RequestMapping(
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name) {
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name, sorry :(");
        }
        if (name.length() > 20) {
            return ResponseEntity.badRequest().body("Too long name, sorry :(");
        }
        if (usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("Already logged in:(");
        }
        usersOnline.put(name, name);
        antiSpamArchive.put(name,new UserMetadata());
        date = new Date();
        String str = "<span style=\"color: green\">" + formatForDateNow.format(date) + "</span>:<span style=\"color: orange\">[" + name + "]</span> logged in";
        messages.add(str);
        historyFile.write(str + '\n');
        return ResponseEntity.ok().build();
    }

    /**
     * curl -i localhost:8080/chat/chat
     */
    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> chat() {
        return new ResponseEntity<>(messages.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n")),
                HttpStatus.OK);
    }

    /**
     * curl -i localhost:8080/chat/online
     */
    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//TODO
    }

    /**
     * curl -X POST -i localhost:8080/chat/logout -d "name=I_AM_STUPID"
     */
    @RequestMapping(
            path = "logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity logout(@RequestParam("name") String name) {
        if (!usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("You aren't logged in!");
        } else {
            usersOnline.remove(name);

            date = new Date();
            String str = "<span style=\"color: green\">" + formatForDateNow.format(date) + "</span>:<span style=\"color: orange\">[" + name + "]</span> " + "Logged out!";
            messages.add(str);
            historyFile.write(str + "\n");
            return ResponseEntity.ok().build();
        }
    }


    /**
     * curl -X POST -i localhost:8080/chat/say -d "name=I_AM_STUPID&msg=Hello everyone in this chat"
     */
    @RequestMapping(
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("name") String name, @RequestParam("msg") String msg) {

        if (!usersOnline.containsKey(name))
            return ResponseEntity.badRequest().build();


        if ( ( System.nanoTime() - antiSpamArchive.get(name).getLastMessageTime() ) / 1000000000.0  < 4 ){
            if (antiSpamArchive.get(name).getNumberOfMessages() == 2)
                return ResponseEntity.ok().build();
            else {
                System.out.println("fail1");
                antiSpamArchive.get(name).incNumberOfMesssages();
            }
        } else {
            System.out.println("fail2");
            antiSpamArchive.get(name).setLastMessageTime();
            antiSpamArchive.get(name).setNumberOfMessagesToZero();
        }

        msg = msg.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(msg);
        int ind = 0;
        while (matcher.find()) {
            sb.append(msg.substring(ind, matcher.start()));
            ind = matcher.end();
            sb.append("<a target=\"_blank\" href=\"" + matcher.group() + "\">" + matcher.group() + "</a>");
        }
        if (ind < msg.length()) {
            sb.append(msg.substring(ind));
        }


        date = new Date();
        String str = "<span style=\"color: green\">" + formatForDateNow.format(date) + "</span>:<span style=\"color: orange\">[" + name + "]</span> " + sb.toString();
        messages.add(str);
        historyFile.write(str + '\n');
        return ResponseEntity.ok().build();
    }
}
