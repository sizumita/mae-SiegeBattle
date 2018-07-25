package mae.siegebattle;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();
    CustomConfig players;
    CustomConfig gear;
    CustomConfig kit;

    public void reload(){
        players.reloadConfig();
        gear.reloadConfig();
        kit.reloadConfig();
    }

    public Config(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.players = new CustomConfig(plugin, "players.yml");
        this.gear = new CustomConfig(plugin, "gear.yml");
        this.kit = new CustomConfig(plugin, "kit.yml");
    }

    public void kit_save(Player p, String kitname) {
        ItemStack[] i = p.getInventory().getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            plugin.kit.getConfig().set(kitname + "." + pos, stack);

            pos++;
        }
        plugin.kit.saveConfig();
        plugin.kit.reloadConfig();
    }

    private String get_path(Player p, String path){
        return p.getUniqueId().toString() + "." + path;
    }

    public String get_prefix(Player p, Boolean hash){
        String uuid = String.valueOf(p.getUniqueId());
        Integer rank = players.getConfig().getInt(uuid + ".rank");
        Integer kill = players.getConfig().getInt(uuid + ".kill");
        String prefix;
        if(hash){
            if(rank < 10){
                prefix = " §l§e[新米]§f ";
            } else if(rank < 20){
                prefix = " §l§e[初心者]§f ";
            } else {
                prefix = "";
            }
            if(kill >= 50){
                prefix += " §c[通り魔]§f ";

            } else if(kill >= 100){
                prefix += " §c[殺し屋]§f ";
            }  else if(kill >= 300){
                prefix += " §c[殺略者]§f ";
            } else if(kill >= 700){
                prefix += " §c[殺人鬼]§f ";
            } else if(kill >= 1000){
                prefix += " §c[悪魔]§f ";
            } else {
                prefix += "";
            }
            if(plugin.battle.blue_king == p){
                prefix = "§9[青の大将] " + prefix;
            } else if(plugin.battle.red_king == p){
                prefix = "§c[赤の大将] " + prefix;
            } else if(plugin.battle.red.contains(p)){
                prefix = "§c[赤チーム] " + prefix;
            } else if(plugin.battle.blue.contains(p)){
                prefix = "§9[青チーム] " + prefix;
            } else {
                prefix += "";
            }
        } else {
            if(rank < 10){
                prefix = " [新米] ";
            } else if(rank < 20){
                prefix = " [初心者] ";
            } else {
                prefix = "";
            }
            if(kill >= 50){
                prefix += " [通り魔] ";

            } else if(kill >= 100){
                prefix += " [殺し屋] ";
            }  else if(kill >= 300){
                prefix += " [殺略者] ";
            } else if(kill >= 700){
                prefix += " [殺人鬼] ";
            } else if(kill >= 1000){
                prefix += " [悪魔] ";
            } else {
                prefix += "";
            }
            if(plugin.battle.blue_king == p){
                prefix = "[青の大将] " + prefix;
            } else if(plugin.battle.red_king == p){
                prefix = "[赤の大将] " + prefix;
            } else if(plugin.battle.red.contains(p)){
                prefix = "[赤チーム] " + prefix;
            } else if(plugin.battle.blue.contains(p)){
                prefix = "[青チーム] " + prefix;
            } else {
                prefix += "";
            }
        }
        return prefix;
    }

    public Integer get_rank(Player p){
//        Bukkit.broadcastMessage(String.valueOf(players.getConfig().getInt(p.getUniqueId().toString() + ".rank", 0)));
        return players.getConfig().getInt(get_path(p, "rank"), 1);
    }

    public String get_kit(Player p){
        return players.getConfig().getString(get_path(p, "kit"), "default");
    }

    public int get_kill(Player p){
        return players.getConfig().getInt(get_path(p, "kill"), 0);
    }

    public int get_death(Player p){
        return players.getConfig().getInt(get_path(p, "death"), 0);
    }

    public String get_kd(Player p){
        if(players.getConfig().getInt(get_path(p, "death"), 0) == 0){
            return "None";
        } else {
            return String.valueOf(get_kill(p) / get_death(p));

        }
    }

    public int get_money(Player p){
        return players.getConfig().getInt(get_path(p, "money"), 0);
    }

    public Boolean add_money(Player p, int money){
        players.getConfig().set(get_path(p, "money"), get_money(p) + money);
        plugin.saveConfig();
        return true;
    }

    public int get_xp(Player p){
        return players.getConfig().getInt(get_path(p, "xp"), 0);
    }

    public boolean add_xp(Player p, int xp){
        players.getConfig().set(get_path(p, "xp"), get_xp(p) + xp);
        plugin.saveConfig();
        return true;
}

    public int xp_to_rank(Player p){
        for (String key : plugin.getConfig().getConfigurationSection("rank").getKeys(false)) {
            int xp = plugin.getConfig().getInt("rank." + key);
            if(get_xp(p) < xp){
                return Integer.parseInt(key) - 1;
            }
        }
        return 3;
    }

    public Boolean remove_money(Player p, int money){
        if(get_money(p) - money < 0) return false;
        players.getConfig().set(get_path(p, "money"), get_money(p) - money);
        plugin.saveConfig();
        return true;
    }

    public List<String> get_player_have_gear_names(Player p){
        String kit = get_kit(p);
        String gear1 = players.getConfig().getString(get_path(p, "gear." + kit + ".1"));
        String gear2 = players.getConfig().getString(get_path(p, "gear." + kit + ".2"));
        String gear3 = players.getConfig().getString(get_path(p, "gear." + kit + ".3"));
        List<String> list = new ArrayList<String>();
        list.add(gear1);
        list.add(gear2);
        list.add(gear3);
        return list;
    }

    public boolean set_player_kit(Player p){
        if(!kit.getConfig().contains("blue_" + get_kit(p)) && !kit.getConfig().contains("red_" + get_kit(p))) return false;
        if(players.getConfig().contains(get_path(p, "gear." + get_kit(p)))) return false;
        players.getConfig().set(get_path(p, "gear." + get_kit(p) + ".1"), "N");
        players.getConfig().set(get_path(p, "gear." + get_kit(p) + ".2"), "N");
        players.getConfig().set(get_path(p, "gear." + get_kit(p) + ".3"), "N");
        plugin.saveConfig();
        return true;
    }

    public Map<String,Integer> get_gear_effect(String gear_name){
        Map<String,Integer> gear_map = new HashMap<String, Integer>();
        String gear_path = gear_name + ".effect";
        int speed = gear.getConfig().getInt(gear_path + ".speed");
        int power = gear.getConfig().getInt(gear_path + ".power");
        gear_map.put("speed", speed);
        gear_map.put("power", power);
        return gear_map;
    }

    public void set_all_member_list_prefix(){
        for(Player player: plugin.getServer().getOnlinePlayers()){
            player.setPlayerListName(plugin.config.get_prefix(player, true) + player.getName());
        }
    }

    public void kit_load(Player p, String name) {
        String uuid = String.valueOf(p.getUniqueId());
        String first_path = "";
        if (name.equals("blue")) {
            first_path = "blue_";
        } else if (name.equals("red")) {
            first_path = "red_";
        }
        String end_path = players.getConfig().getString(String.format("%s.kit", uuid));
        String path = first_path + end_path;
//        Set<String> sl = players.getConfig().getConfigurationSection(path).getKeys(false);
        for (String s : kit.getConfig().getConfigurationSection(path).getKeys(false)) {
            ItemStack item = kit.getConfig().getItemStack(path + "." + s);
            p.getInventory().setItem(Integer.parseInt(s), item);
        }

    }

}
