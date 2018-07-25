package mae.siegebattle.listener;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogoutListener implements Listener {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();

    public LogoutListener(SiegeBattle plugin){
        this.plugin = plugin;
        hook.setWebhook(plugin.webhook);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        event.setQuitMessage("");
        hook.sendLeaveMessage(player);
        if(plugin.battle.start_battle && plugin.battle.blue_king == player){
            Bukkit.broadcastMessage("§4§l青チームの大将が抜けてしまった！");
            plugin.battle.finish_battle("red");
        }
    }
}
