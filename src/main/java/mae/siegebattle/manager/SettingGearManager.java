package mae.siegebattle.manager;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;

public class SettingGearManager {
    @Getter
    private final SiegeBattle plugin;
    private DiscordHook hook = new DiscordHook();
    private Config config;

    public SettingGearManager(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.config = new Config(plugin);
    }
}
