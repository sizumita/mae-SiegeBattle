package mae.siegebattle;

import lombok.Getter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Bossbar {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();
    Config config;
    public BossBar blue_bar;

    public Bossbar(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.config = new Config(plugin);
    }

    public void CreateBossbar(Player blue){
        blue_bar = plugin.getServer().createBossBar(blue.getPlayerListName(), BarColor.BLUE, BarStyle.SEGMENTED_10);
    }

    public void ChangeToPlayerHealth(Player player, Boolean dead){
        blue_bar.addPlayer(player);
        if(dead){
            blue_bar.setProgress(0);
            blue_bar.removeAll();
            return;
        }
        blue_bar.removeAll();
        blue_bar.setProgress(player.getHealth() / 20.0);
    }

}
