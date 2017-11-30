package hello.reddwarf.server;

import com.sun.sgs.app.*;
import com.sun.sgs.app.util.ScalableHashMap;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public class Player implements Serializable, ManagedObject, ClientSessionListener {

    private final static Random random = new Random();

    public final String name;

    private int score;

    // Ссылка на сессию, через которую можно отправлять сообщения на клиент
    private ManagedReference<ClientSession> sessionRef;

    // Ссылка на сервер для доступа к списку онлайн-игроков
    private ManagedReference<Server> serverRef;

    // Ссылка на текущую битву. Если игрок не в битве - значение этого поля null
    private ManagedReference<Battle> battleRef;
    private Logger logger;

    public Player(String name, Server server) {
        this.name = name;
        serverRef = AppContext.getDataManager().createReference(server);
        score = 0;
    }

    @Override
    public void receivedMessage(ByteBuffer byteBuffer) {
        // При получении сообщения разбираем его и вызываем соответствующий метод
        String message = Messages.decodeString(byteBuffer);
        if (message.equals("PLAY")) {
            play();
        } else if (message.equals("ROCK")) {
            answer(Weapon.ROCK);
        } else if (message.equals("PAPER")) {
            answer(Weapon.PAPER);
        } else if (message.equals("SCISSORS")) {
            answer(Weapon.SCISSORS);
        }
    }

    @Override
    public void disconnected(boolean b) {
        serverRef.get().disconnect(this);
    }

    private void answer(Weapon weapon) {
        if (battleRef != null) {
            battleRef.getForUpdate().answer(this, weapon);
        }
    }

    private void play() {
        logger.info("Choosing enemy for "+name);
        // Выберем случайного игрока из списка онлайн и начнем битву
        Player target = getRandomPlayer();
        if (target != null && target.battleRef == null) {
            Battle battle = new Battle(this, target);
            this.sessionRef.get().send(Messages.encodeString("BATTLE " + target.name));
            target.sessionRef.get().send(Messages.encodeString("BATTLE " + this.name));
            target.battleRef = AppContext.getDataManager().createReference(battle);
            this.battleRef = target.battleRef;
            battle.start();
        } else {
            this.sessionRef.get().send(Messages.encodeString("ERROR"));
        }
    }

    /**
     * Поиск случайного соперника (кроме самого игрока)
     * Если никого найти не удалось, возвращается null
     * @return случайный соперник или null, если не найден
     */
    private Player getRandomPlayer() {
        ScalableHashMap<String,Player> onlineMap = serverRef.get().onlinePlayersRef.get();
        Set<String> namesSet = new HashSet<String>(onlineMap.keySet());
        namesSet.remove(name);
        if (namesSet.isEmpty()) {
            return null;
        } else {
            ArrayList<String> namesList = new ArrayList<String>(namesSet);
            String randomName =  namesList.get(random.nextInt(namesList.size()));
            return onlineMap.get(randomName);
        }
    }

    public void connected() {
        // При подключении к серверу сообщим клиенту, сколько у нас очков
        sessionRef.get().send(Messages.encodeString("SCORE " + score));
    }

    /**
     * Бой закончен, игрок уведомляется о результате боя
     */
    public void battleResult(Battle.Result result) {
        switch (result) {
            case DRAW:
                score+=1;
                sessionRef.get().send(Messages.encodeString("DRAW"));
                break;
            case WON:
                score+=2;
                sessionRef.get().send(Messages.encodeString("WON"));
                break;
            case LOST:
                sessionRef.get().send(Messages.encodeString("LOST"));
                break;
        }
        sessionRef.get().send(Messages.encodeString("SCORE " + score));
        battleRef = null;
    }

    public void setSession(ClientSession session) {
        sessionRef = AppContext.getDataManager().createReference(session);
    }
}