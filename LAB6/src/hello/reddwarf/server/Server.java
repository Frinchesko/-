/**
 * Created by Пошка on 28.11.2017.
 */
package hello.reddwarf.server;

import com.sun.sgs.app.*;
import com.sun.sgs.app.util.ScalableHashMap;

import java.io.Serializable;
import java.util.Properties;

/**
 * Сервер игры. Этот класс автоматически загружается платформой,
 * инициализируется и его платформа уведомляет о новых подключениях.
 */
public class Server implements AppListener, Serializable, ManagedObject {

    public ManagedReference<ScalableHashMap<String, Player>> onlinePlayersRef;

    @Override
    public void initialize(Properties props) {

        // Создаем коллекцию для игроков онлайн
        ScalableHashMap<String, Player> onlinePlayers = new ScalableHashMap<String, Player>();
        onlinePlayersRef = AppContext.getDataManager().createReference(onlinePlayers);

    }

    @Override
    public ClientSessionListener loggedIn(ClientSession session) {

        String name = session.getName();

        // Подключился пользователь. Необходимо загрузить его из базы данных, либо зарегистрировать нового
        Player player = loadOrRegister(name);

        // Установим игроку сессию. Сессия - это объект, через который осуществляется
        // сетевое взаимодействие - отсылка сообщений на клиент
        player.setSession(session);
        // Уведомляем игрока о том, что он подключился
        player.connected();
        // Добавим его в список онлайн-игроков
        onlinePlayersRef.get().put(player.name, player);

        return player;
    }
    private Player loadOrRegister(String name) {
        try {
            return (Player) AppContext.getDataManager().getBindingForUpdate("player." + name);
        } catch (NameNotBoundException e) {
            // Попытка загрузить объект и перехват исключения -
            // единственный способ узнать, есть ли такой объект в базе
            Player player = new Player(name, this);
            AppContext.getDataManager().setBinding("player." + name, player);
            return player;
        }
    }

    public void disconnect(Player player) {
    }
}