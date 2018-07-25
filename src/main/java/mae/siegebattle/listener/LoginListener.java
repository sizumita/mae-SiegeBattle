package mae.siegebattle.listener;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();
    Config config;


    public LoginListener(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.config = new Config(plugin);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = String.valueOf(event.getPlayer().getUniqueId());
        if(!plugin.players.getConfig().contains(uuid)){
            event.setJoinMessage(ChatColor.YELLOW + "§l082! " + event.getPlayer().getDisplayName() + "さんが初ログインしました！");
        } else {
            event.setJoinMessage(ChatColor.YELLOW + "§l" + event.getPlayer().getDisplayName() + "さんがログインしました！\n" +
                    "ランク:" + String.valueOf(plugin.players.getConfig().getInt(String.format("%s.rank", uuid),1)));
        }
        plugin.players.getConfig().set(String.format("%s.rank", uuid), config.get_rank(player));
        plugin.players.getConfig().set(String.format("%s.kit", uuid),  config.get_kit(player));
        plugin.players.getConfig().set(String.format("%s.kill", uuid), config.get_kill(player));
        plugin.players.getConfig().set(String.format("%s.death", uuid), config.get_death(player));
        plugin.players.getConfig().set(String.format("%s.money", uuid), config.get_money(player));
        plugin.players.getConfig().set(String.format("%s.xp", uuid), config.get_xp(player));
        plugin.players.saveConfig();
        config.reload();
        config.set_player_kit(player);

        plugin.players.getConfig().set(String.format("%s.name", uuid), player.getName());
        plugin.players.saveConfig();
        plugin.players.reloadConfig();
        String prefix = config.get_prefix(player, true);
        player.setPlayerListName(prefix + player.getName());
        player.teleport(plugin.spawnLocation);
        hook.sendLoginMessage(player);
        if(plugin.battle.start_battle){
            player.setGameMode(GameMode.SPECTATOR);
        }
        plugin.manager.reloadScoreBoard(player);

    }

}
