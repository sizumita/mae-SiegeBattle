package mae.siegebattle.listener;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();

    public InventoryClickListener(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
    }

    @EventHandler
    public void  onInventoryClickEvent(InventoryClickEvent e)   {
        if(!ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("キットメニュー")) return;

        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || !e.getCurrentItem().hasItemMeta()){
            player.closeInventory();
            return;
        }
    }
}
