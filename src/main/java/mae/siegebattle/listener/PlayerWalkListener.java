package mae.siegebattle.listener;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerWalkListener implements Listener {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();

    public PlayerWalkListener(SiegeBattle plugin){
        this.plugin = plugin;
        hook.setWebhook(plugin.webhook);
    }

    @EventHandler
    public void onPlayerWalk(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
        if(plugin.battle.waiting){
            event.setCancelled(true);
        }

    }
}
