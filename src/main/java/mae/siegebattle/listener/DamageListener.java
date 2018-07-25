package mae.siegebattle.listener;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();
    Config config;

    public DamageListener(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.config = new Config(plugin);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player){
            Player dameged = ((Player) event.getEntity()).getPlayer();
            if(plugin.battle.starting_battle){
                if(plugin.battle.blue.contains(plugin.getServer().getPlayer(event.getDamager().getUniqueId())) && plugin.battle.blue.contains(((Player) event.getEntity()).getPlayer())){
                    event.setCancelled(true);
                    return;
                } else if(plugin.battle.red.contains(plugin.getServer().getPlayer(event.getDamager().getUniqueId())) && plugin.battle.red.contains(((Player) event.getEntity()).getPlayer())) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getFinalDamage() == 0){
                    return;
                }
                if (event.getDamager() instanceof Player){
                    Player damager_player = plugin.getServer().getPlayer(event.getDamager().getUniqueId());
                    damager_player.sendMessage(ChatColor.YELLOW + String.format("あなたは%.2fダメージを与えた！", event.getFinalDamage()));
                }
                ((Player) event.getEntity()).getPlayer().sendMessage(ChatColor.RED + String.format("あなたは%.2fダメージを受けた！", event.getFinalDamage()));

            } else {
                event.setCancelled(true);
            }

        } else if(!event.getDamager().isOp()) {
            event.setCancelled(true);
        }
    }
}
