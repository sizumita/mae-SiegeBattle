package mae.siegebattle.listener;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.KanaConverter;
import mae.siegebattle.SiegeBattle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();
    DiscordHook red_hook = new DiscordHook();
    DiscordHook blue_hook = new DiscordHook();
    Config config;

    public ChatListener(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.config = new Config(plugin);
        this.red_hook.setWebhook("https://discordapp.com/api/webhooks/469062531022061569/WwjYndOtwSMCalKlpj5RytkhAKI9zYEAbMcipPDeQ2O8DxMx8eTxG5Rifs77FkRr_T3y");
        this.blue_hook.setWebhook("https://discordapp.com/api/webhooks/469062797007912970/k3ZCILNXnryZ3DBa5d6GzfSpbUNuRBOvGrm4PZI4Y4d6W5xkqvUNyURuow1znBUHY0Wd");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        String prefix = config.get_prefix(event.getPlayer(), false);
        Player player = event.getPlayer();
        event.setMessage(KanaConverter.conv(event.getMessage()) + "§o§7  (" + event.getMessage() + ")");
        event.setFormat(player.getPlayerListName() + " : " + event.getMessage());
        if (plugin.battle.starting_battle && !event.getMessage().startsWith("！")) {
            event.setCancelled(true);
            if (plugin.battle.red.contains(player)) {
                for (Player p : plugin.battle.red) {
                    p.sendMessage("§l[team]" + player.getPlayerListName() + " : " + event.getMessage());
                }
                red_hook.sendChatWebhook(event.getMessage(), event.getPlayer().getUniqueId().toString(), prefix + player.getName());
            } else if (plugin.battle.blue.contains(player)) {
                for (Player p : plugin.battle.blue) {
                    p.sendMessage("§l[team]" + player.getPlayerListName() + " : " + event.getMessage());
                }
                blue_hook.sendChatWebhook(event.getMessage(), event.getPlayer().getUniqueId().toString(), prefix + player.getName());
            }
        }

        if (!event.isCancelled()) {
            hook.sendChatWebhook(event.getMessage(), event.getPlayer().getUniqueId().toString(), prefix + player.getName());
        }

    }
}

