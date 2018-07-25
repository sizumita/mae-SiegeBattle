package mae.siegebattle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SelfCancelingTask extends BukkitRunnable {

    private final SiegeBattle plugin;

    private int counter;

    private int uniuqe_id;

    public SelfCancelingTask(SiegeBattle plugin, int counter, int uniuqe_id) {
        this.plugin = plugin;
        if (counter < 1) {
            throw new IllegalArgumentException("counter には1以上を指定してください。");
        } else {
            this.counter = counter;
            this.uniuqe_id = uniuqe_id;
        }
    }

    @Override
    public void run() {
        // ここに、スケジュールの処理内容を実装します。
        if (counter > 0) {
            if(plugin.battle.battle_id != uniuqe_id) {this.cancel(); return;}
            plugin.battle.time -= 1;
            if(plugin.battle.time == 0 ) {plugin.battle.finish_battle("blue"); this.cancel(); return;}
            for (Player player: plugin.getServer().getOnlinePlayers()){
                plugin.manager.reloadScoreBoard(player);
            }

        } else {
            plugin.battle.time = 300;
            this.cancel();
        }
    }
}