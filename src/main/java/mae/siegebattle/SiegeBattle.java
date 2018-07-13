package mae.siegebattle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class SiegeBattle extends JavaPlugin implements Listener {
    Boolean start_battle = false;
    Boolean waiting = false;
    Boolean starting_battle = false;
    Boolean preset_battle = true;
    ArrayList<String> battle_list = new ArrayList<String>();
    CustomConfig field;
    CustomConfig players;
    CustomConfig kit;
    ArrayList<Player> blue = new ArrayList<>();
    ArrayList<Player> red = new ArrayList<>();
    TitleSender TitleSender;


    @Override
    public void onEnable() {
        // Plugin startup logic
        field = new CustomConfig(this, "field.yml");
        kit = new CustomConfig(this, "kit.yml");
        players = new CustomConfig(this, "players.yml");
        kit.saveDefaultConfig();
        players.saveDefaultConfig();
        field.saveDefaultConfig();
        battle_list.add("砂の惑星 Sand planet");
        battle_list.add("王国 Kingdom");
        battle_list.add("空島 Sky Island");
        battle_list.add("オフィス街 office Street");
        getServer().getPluginManager().registerEvents(this, this);
        TitleSender = new TitleSender();
        kit.reloadConfig();
        field.reloadConfig();
        players.reloadConfig();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        field.saveConfig();
        players.saveConfig();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(!player.isOp()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "OP以外はブロックを壊すことはできません！");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if(blue.contains(event.getPlayer())){
            event.setMessage(ChatColor.BLUE + "[青チーム]" + event.getPlayer().getName() + ChatColor.WHITE + event.getMessage());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerWalk(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
        if(waiting){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Bukkit.broadcastMessage(event.getEntity().getPlayer().getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
        event.setJoinMessage("§l" + event.getPlayer().getDisplayName() + "さんがログインしました！");
        String uuid = String.valueOf(event.getPlayer().getUniqueId());
        players.getConfig().set(String.format("%s.rank", uuid), players.getConfig().getInt(String.format("%s.rank", uuid),1));
        players.getConfig().set(String.format("%s.kit", uuid), players.getConfig().getString(String.format("%s.kit", uuid), "default"));
        players.getConfig().set(String.format("%s.kill", uuid), players.getConfig().getInt(String.format("%s.kill", uuid),0));
        players.getConfig().set(String.format("%s.death", uuid), players.getConfig().getInt(String.format("%s.death", uuid),0));
        players.saveConfig();
        players.reloadConfig();



    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player){
            ((Player) event.getEntity()).getPlayer();
            if(starting_battle){
                if(blue.contains(getServer().getPlayer(event.getDamager().getUniqueId())) && blue.contains(((Player) event.getEntity()).getPlayer())){
                    event.setCancelled(true);
                    return;
                } else if(red.contains(getServer().getPlayer(event.getDamager().getUniqueId())) && red.contains(((Player) event.getEntity()).getPlayer())) {
                    event.setCancelled(true);
                    return;
                }
                if ((int)event.getFinalDamage() == 0){
                    return;
                }
                if (event.getDamager() instanceof Player){
                    Player damaged_player = getServer().getPlayer(event.getDamager().getUniqueId());
                    damaged_player.sendMessage(ChatColor.YELLOW + String.format("あなたは%fダメージを与えた！", event.getFinalDamage()));
                }
                ((Player) event.getEntity()).getPlayer().sendMessage(ChatColor.RED + String.format("あなたは%fダメージを受けた！", event.getFinalDamage()));

            } else {
                event.setCancelled(true);
            }

        } else if(!event.getDamager().isOp()) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player p = (Player) sender;
        String command = args[0];

        if (cmd.getName().equals("battle")){
            if (args.length == 0) {
                showHelp(sender);
                return false;
            }

            if (command.equalsIgnoreCase("start")) {
                TitleSender.sendTitle(p, "募集が開始されました", "20秒後に募集終了します", "そのままにしていれば参加可能です。");
                Start_battle(p);
                return true;
            }

            if (command.equalsIgnoreCase("stop")) {
                Stop_battle(p);
                return true;
            }

            if (command.equalsIgnoreCase("end")) {
                End_battle(p);
                return true;
            }

            if (command.equalsIgnoreCase("notice")) {
                TitleSender.sendTitle(p, args[1], args[2], args[3]);
                return true;
            }
        } else if (cmd.getName().equals("user")){
            if (command.equals("info")){
                showUser(p);
            }

        } else if (cmd.getName().equals("kit") && p.isOp()){
            if (command.equalsIgnoreCase("new")) {
                kit_save(p, args[1]);
                Bukkit.broadcastMessage("kitを保存しました");
                return true;
            }
            if (command.equalsIgnoreCase("test")) {
                kit_load(p, args[1]);
                return true;
            }
        }


        return false;
    }

    void showHelp(CommandSender p) {
        String help_message = "/battle start バトルをスタートします。\n" +
                "/battle stop バトルをストップします。";
        p.sendMessage(help_message);
    }

    void showUser(Player p){
        p.sendMessage(ChatColor.AQUA + String.format("%sの情報", p.getName()));
        p.sendMessage(ChatColor.AQUA + String.format("kit: %s", players.getConfig().getString(String.valueOf(p.getUniqueId()) + ".kit")));
    }

    void Stop_battle(Player p) {
        preset_battle = false;
        p.sendMessage("停止");
    }

    void End_battle(Player p) {
        preset_battle = false;
        p.sendMessage("停止");
        for (Player s : getServer().getOnlinePlayers()){
            s.setPlayerListName(s.getName());
        }
        start_battle = false;
        waiting = false;
        starting_battle = false;
        preset_battle = true;
        for(Player player : blue){
            player.getInventory().clear();
        }
        for(Player player : red){
            player.getInventory().clear();
        }
        blue = new ArrayList<>();
        red = new ArrayList<>();
    }

    void Start_battle(Player player) {
        if (start_battle) {
            return;
        }
        start_battle = true;
        for (Player s : getServer().getOnlinePlayers()){
            TitleSender.sendTitle(s, "募集が開始されました", "20秒後に募集終了します", "そのままにしていれば参加可能です。");
        }
        if (getServer().getOnlinePlayers().size() >= 2) {
            Bukkit.broadcastMessage(ChatColor.AQUA + String.format("バトルを開始します。参加人数%d人", getServer().getOnlinePlayers().size()));
            battle(player);
            return;
        }
        Bukkit.broadcastMessage(ChatColor.AQUA + "参加者を募集しています...しばらくお待ちください...");
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                //Your code here!

                if (getServer().getOnlinePlayers().size() < 2) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + "参加人数が足りず、募集を停止しました。");
                    start_battle = false;
                    return;
                } else if(preset_battle){
                    Bukkit.broadcastMessage(ChatColor.AQUA + String.format("バトルを開始します。参加人数%d人", getServer().getOnlinePlayers().size()));
                    battle(player);
                    return;
                }
            }
        }, 200);

    }

    void battle(Player p){
        Bukkit.broadcastMessage(ChatColor.AQUA + "参加者を赤チームと青チームに振り分けます...しばらくお待ちください...");
        ArrayList<Player> setplayers = new ArrayList<>();
        setplayers.addAll(getServer().getOnlinePlayers());
        Integer size;
        if(setplayers.size() % 2 == 0){
            size = setplayers.size() / 2;
        } else {
            size = (setplayers.size() - 1) / 2;
        }
//        Collections.shuffle(players);
        for(int i = 0; i < setplayers.size(); ++i){
            Player s = (Player) setplayers.get(i);
            if(i <= size - 1){
                red.add(s);
                s.sendMessage(ChatColor.AQUA + "あなたは赤チームに配属されました！");
                s.setPlayerListName(ChatColor.RED + "[赤チーム] " + s.getName());
                s.getInventory().clear();
                kit_load(s, "red");
                s.setGameMode(GameMode.SPECTATOR);
            } else {
                blue.add(s);
                s.sendMessage(ChatColor.AQUA + "あなたは青チームに配属されました！");
                s.setPlayerListName(ChatColor.BLUE + "[青チーム] " + s.getName());
                s.getInventory().clear();
                kit_load(s, "blue");
                s.setGameMode(GameMode.SPECTATOR);
            }

        }
        Collections.shuffle(battle_list);
        for (Player s : getServer().getOnlinePlayers()){
            TitleSender.sendTitle(s, battle_list.get(0), "10秒後にゲームを開始します", "");
        }
//        battle_list.add("砂の惑星 Sand planet");
//        battle_list.add("王国 Kingdom");
//        battle_list.add("空島 Sky Island\n");
//        battle_list.add("オフィス街 office Street\n");
        Integer map_red_x;
        Integer map_red_y;
        Integer map_red_z;
        Integer map_blue_x;
        Integer map_blue_y;
        Integer map_blue_z;
        String map_name;

        if (battle_list.get(0).equals("砂の惑星 Sand planet")){
            map_red_x = field.getConfig().getInt("fields.red.1.x");
            map_red_y = field.getConfig().getInt("fields.red.1.y");
            map_red_z = field.getConfig().getInt("fields.red.1.z");
            map_blue_x = field.getConfig().getInt("fields.blue.1.x");
            map_blue_y = field.getConfig().getInt("fields.blue.1.y");
            map_blue_z = field.getConfig().getInt("fields.blue.1.z");
            map_name = "砂の惑星 Sand planet";
        } else if (battle_list.get(0).equals("王国 Kingdom")){
            map_red_x = field.getConfig().getInt("fields.red.2.x");
            map_red_y = field.getConfig().getInt("fields.red.2.y");
            map_red_z = field.getConfig().getInt("fields.red.2.z");
            map_blue_x = field.getConfig().getInt("fields.blue.2.x");
            map_blue_y = field.getConfig().getInt("fields.blue.2.y");
            map_blue_z = field.getConfig().getInt("fields.blue.2.z");
            map_name = "王国 Kingdom";
        } else if (battle_list.get(0).equals("空島 Sky Island")){
            map_red_x = field.getConfig().getInt("fields.red.3.x");
            map_red_y = field.getConfig().getInt("fields.red.3.y");
            map_red_z = field.getConfig().getInt("fields.red.3.z");
            map_blue_x = field.getConfig().getInt("fields.blue.3.x");
            map_blue_y = field.getConfig().getInt("fields.blue.3.y");
            map_blue_z = field.getConfig().getInt("fields.blue.3.z");
            map_name = "空島 Sky Island";
        } else if (battle_list.get(0).equals("オフィス街 office Street")){
            map_red_x = field.getConfig().getInt("fields.red.4.x");
            map_red_y = field.getConfig().getInt("fields.red.4.y");
            map_red_z = field.getConfig().getInt("fields.red.4.z");
            map_blue_x = field.getConfig().getInt("fields.blue.4.x");
            map_blue_y = field.getConfig().getInt("fields.blue.4.y");
            map_blue_z = field.getConfig().getInt("fields.blue.4.z");
            map_name = "オフィス街 office Street";
        }
//        ここで移動させて、waiting true
        Bukkit.broadcastMessage(ChatColor.AQUA + "10秒後にゲームを開始します..しばらくお待ちください。");
        waiting = true;
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                //Your code here!
                for (Player s : getServer().getOnlinePlayers()){
                    TitleSender.sendTitle(s, "バトル開始！", "", "");
                    s.setGameMode(GameMode.SURVIVAL);
                }
                waiting = false;
                starting_battle = true;


            }
        }, 200);
    }

    public void kit_save(Player p, String kitname) {
        ItemStack[] i = p.getInventory().getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            kit.getConfig().set(kitname + "." + pos, stack);

            pos++;
        }
        kit.saveConfig();
        kit.reloadConfig();
    }

    public void kit_load(Player p, String name){
        String uuid = String.valueOf(p.getUniqueId());
        String first_path = "";
        if(name.equals("blue")){
            first_path = "blue_";
        } else if(name.equals("red")){
            first_path = "red_";
        }
        String end_path = players.getConfig().getString(String.format("%s.kit", uuid));
        String path = first_path + end_path;
//        Set<String> sl = players.getConfig().getConfigurationSection(path).getKeys(false);
        for (String s: kit.getConfig().getConfigurationSection(path).getKeys(false)){
            ItemStack item = kit.getConfig().getItemStack(path + "." + s);
            p.getInventory().setItem(Integer.parseInt(s), item);
        }

    }
}

