package mae.siegebattle.listener;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();
    Config config;

    public DeathListener(SiegeBattle plugin){
        this.plugin = plugin;
        hook.setWebhook(plugin.webhook);
        config = new Config(plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player killed = event.getEntity().getPlayer();
        Player killer = event.getEntity().getKiller().getPlayer();
        event.setDeathMessage("§l§4" + killed.getName() + " は、 " + killer.getName() + "の [{}] で殺された！".replace("{}", killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName()));
        killed.setGameMode(GameMode.SPECTATOR);
        killed.setHealth(20);
        if(event.getEntity().getKiller() instanceof Player){
            Integer kill_before = plugin.players.getConfig().getInt(killer.getUniqueId() + ".kill");
            plugin.players.getConfig().set(killer.getUniqueId() + ".kill", kill_before + 1);
//            plugin.kill_num.computeIfPresent(killer, (k, s) -> s + 1);
            if(plugin.battle.kill_num.get(killer) == null){
                plugin.battle.kill_num.put(killer, 1);
            } else {
                plugin.battle.kill_num.replace(killer, plugin.battle.kill_num.get(killer) + 1);
            }
            Integer death_before = plugin.players.getConfig().getInt(killed.getUniqueId() + ".death");
            plugin.players.getConfig().set(killed.getUniqueId() + ".death", death_before + 1);
            plugin.players.saveConfig();
            plugin.players.reloadConfig();
            plugin.manager.reloadScoreBoard(killed);
            plugin.manager.reloadScoreBoard(killer);
        }
        if(plugin.battle.starting_battle){
            if(plugin.battle.blue.contains(killed)){
                plugin.battle.blue_resoult_kill += 1;
                plugin.battle.blue.remove(killed);
                killed.setPlayerListName(config.get_prefix(killed, true) + killed.getName());
                killed.teleport(new Location(Bukkit.getWorld("battle"), plugin.battle.map_blue_x, plugin.battle.map_blue_y, plugin.battle.map_blue_z));
            } else if(plugin.battle.red.contains(killed)){
                plugin.battle.red_resoult_kill += 1;
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if(!plugin.battle.starting_battle){return;}
                        killed.teleport(new Location(Bukkit.getWorld("battle"), plugin.battle.map_red_x, plugin.battle.map_red_y, plugin.battle.map_red_z));
                        killed.setGameMode(GameMode.SURVIVAL);
                    }

                }, 100);
            }
            if(plugin.battle.blue_king == killed){
//                plugin.bar.ChangeToPlayerHealth(killed, false);
                plugin.battle.finish_battle("red");
            }
        }
    }
}
