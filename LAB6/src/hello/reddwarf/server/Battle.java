/**
 * Created by Пошка on 28.11.2017.
 */
package hello.reddwarf.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;

import java.io.Serializable;

public class Battle implements ManagedObject, Serializable {

    // Битва длится 5 секунд
    private static final long BATTLE_TIME_MS = 5000;

    enum Result {
        DRAW,
        WON,
        LOST
    }

    private boolean active;

    private ManagedReference<Player> starterPlayerRef;
    private ManagedReference<Player> invitedPlayerRef;

    private Weapon starterWeapon = null;
    private Weapon invitedWeapon = null;

    public Battle(Player starterPlayer, Player invitedPlayer) {
        starterPlayerRef = AppContext.getDataManager().createReference(starterPlayer);
        invitedPlayerRef = AppContext.getDataManager().createReference(invitedPlayer);
        active = false;
    }

    /**
     * Начало игры.
     * Запускается игра, через BATTLE_TIME_MS мс она будет завершена.
     */
    public void start(){
        active = true;
        AppContext.getTaskManager().scheduleTask(new BattleTimeout(this), BATTLE_TIME_MS);
    }

    /**
     * Игрок дал свой ответ.
     * Записываем ответ, данный игроком.
     * @param player - игрок
     * @param weapon - его ответ
     */
    public void answer(Player player, Weapon weapon){
        if (active) {
            if (player.name.equals(starterPlayerRef.get().name)) {
                starterWeapon = weapon;
            } else {
                invitedWeapon = weapon;
            }
        }
    }

    /**
     * Битва завершена.
     * Подводим итоги.
     */
    private void finish() {
        active = false;
        Player starterPlayer = starterPlayerRef.getForUpdate();
        Player invitedPlayer = invitedPlayerRef.getForUpdate();

        if (starterWeapon != null && starterWeapon.beats(invitedWeapon)) {
            starterPlayer.battleResult(Result.WON);
            invitedPlayer.battleResult(Result.LOST);
        } else if (invitedWeapon != null && invitedWeapon.beats(starterWeapon)) {
            invitedPlayer.battleResult(Result.WON);
            starterPlayer.battleResult(Result.LOST);
        } else {
            starterPlayer.battleResult(Result.DRAW);
            invitedPlayer.battleResult(Result.DRAW);
        }

        AppContext.getDataManager().removeObject(this);
    }

    /**
     * Задача, завершаюшая игру по прошествии заданного времени.
     */
    private static class BattleTimeout implements Serializable, Task {

        private ManagedReference<Battle> battleRef;

        public BattleTimeout(Battle battle) {
            battleRef = AppContext.getDataManager().createReference(battle);
        }

        @Override
        public void run() throws Exception {
            battleRef.getForUpdate().finish();
        }
    }
}