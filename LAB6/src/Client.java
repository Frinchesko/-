package
import hello.reddwarf.server.Messages;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * Created by Пошка on 28.11.2017.
 */
public class Client implements SimpleClientListener  {

    private SimpleClient simpleClient;
    private final String host;
    private final String username;
    public static final String DEFAULT_PORT = "62964";

    private final ClientFrame frame;

    public Client(String host, String username, ClientFrame frame) {
        this.host = host;
        this.username = username;
        this.frame = frame;
        simpleClient = new SimpleClient(this);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, new char[]{});
    }

    @Override
    public void loggedIn() {
        frame.loggedIn();
    }

    @Override
    public void loginFailed(String s) {
        frame.setStatus("login failed " + username+": "+s);
    }

    @Override
    public void receivedMessage(ByteBuffer packet) {

        String text = Messages.decodeString(packet);

        if (text.startsWith("SCORE")) {
            frame.setScore(text);
        } else if (text.startsWith("BATTLE")) {
            frame.startBattle(text);
        } else if (text.startsWith("DRAW")) {
            frame.setBattleResult(text);
        } else if (text.startsWith("WON")) {
            frame.setBattleResult(text);
        } else if (text.startsWith("LOST")) {
            frame.setBattleResult(text);
        } else if (text.startsWith("ERROR")) {
            frame.setStatus(text);
        }
    }

    public void login() {
        try {
            Properties connectProps = new Properties();
            connectProps.put("host", host);
            connectProps.put("port", DEFAULT_PORT);
            simpleClient.login(connectProps);
        } catch (Exception e) {
            e.printStackTrace();
            disconnected(false, e.getMessage());
        }

    }

    public void play() {
        try {
            simpleClient.send(Messages.encodeString("PLAY"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void answer(String text) {
        try {
            simpleClient.send(Messages.encodeString(text));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}