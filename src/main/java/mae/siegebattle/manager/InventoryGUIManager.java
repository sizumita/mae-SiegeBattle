package mae.siegebattle.manager;

import lombok.Getter;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryGUIManager {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();

    public InventoryGUIManager(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
    }

    public void openGUI_kit_menu(Player player){
        Inventory i = Bukkit.createInventory(null, 9, ChatColor.AQUA + "キットメニュー");
        ItemStack list = new ItemStack(Material.PAPER);
        ItemMeta list_Meta = list.getItemMeta();
        list_Meta.setDisplayName(ChatColor.BLUE + "§l所持しているキット一覧");
        list.setItemMeta(list_Meta);
    }
}
