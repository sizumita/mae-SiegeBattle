package mae.siegebattle;

import mae.siegebattle.listener.*;
import mae.siegebattle.manager.*;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public final class SiegeBattle extends JavaPlugin implements Listener {
    public CustomConfig field;
    public CustomConfig players;
    public CustomConfig kit;
    public ArrayList<Player> blue = new ArrayList<>();
    public ArrayList<Player> canBuild = new ArrayList<>();
    public ArrayList<Player> red = new ArrayList<>();
    public TitleSender TitleSender;
    public String webhook;
    public Location spawnLocation;
    public scoreboardManager manager = new scoreboardManager(this);
    DiscordHook hook = new DiscordHook();
    public Config config = new Config(this);
    public Battle_control battle;



    @Override
    public void onEnable() {
        // Plugin startup logic

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        field = new CustomConfig(this, "field.yml");
        kit = new CustomConfig(this, "kit.yml");
        players = new CustomConfig(this, "players.yml");
//        kit.saveDefaultConfig();
//        players.saveDefaultConfig();
//        field.saveDefaultConfig();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        TitleSender = new TitleSender();
        kit.reloadConfig();
        field.reloadConfig();
        players.reloadConfig();
        spawnLocation = getServer().getWorld("battle").getSpawnLocation();
        webhook = getConfig().getString("webhook");
        hook.setWebhook(webhook);
        Random rand = new Random();
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        getServer().getPluginManager().registerEvents(new LogoutListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerWalkListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BreakListener(this), this);
        battle = new Battle_control(this);
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // ここに処理を実装する。
                for (Player s: getServer().getOnlinePlayers()){
                    manager.reloadScoreBoard(s);
                }
            }
        }, 0L, 200L);
        
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        battle.End_battle();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(!event.getPlayer().isOp()) {
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
                battle.Start_battle();
                return true;
            }

            if (command.equalsIgnoreCase("stop")) {
                Stop_battle(p);
                return true;
            }

            if (command.equalsIgnoreCase("end")) {
                battle.End_battle();
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
            if(p.isOp()){
                if (command.equals("add")) {
                    Player c = Bukkit.getPlayer(args[1]);
                    canBuild.add(c);
                }
            }

        } else if (cmd.getName().equals("kit") && p.isOp()){
            if (command.equalsIgnoreCase("new")) {
                config.kit_save(p, args[1]);
                Bukkit.broadcastMessage("kitを保存しました");
                return true;
            }
            if (command.equalsIgnoreCase("test")) {
                config.kit_load(p, args[1]);
                return true;
            }
        }


        return false;
    }

    void showHelp(CommandSender p) {
        String help_message = "/battle start バトルをスタートします。\n" +
                "/battle stop バトルをストップします。\n" +
                "/battle end バトルを強制終了します。";
        p.sendMessage(help_message);
    }

    void showUser(Player p){
        p.sendMessage(ChatColor.AQUA + String.format("%sの情報", p.getName()));
        p.sendMessage(ChatColor.AQUA + String.format("kit: %s", players.getConfig().getString(String.valueOf(p.getUniqueId()) + ".kit")));
        p.sendMessage(ChatColor.AQUA + String.format("kill: %s", String.valueOf(players.getConfig().getInt(String.valueOf(p.getUniqueId()) + ".kill"))));
        p.sendMessage(ChatColor.AQUA + String.format("death: %s", String.valueOf(players.getConfig().getInt(String.valueOf(p.getUniqueId()) + ".death"))));
        if(players.getConfig().getInt(String.valueOf(p.getUniqueId()) + ".death") == 0){
            p.sendMessage(ChatColor.AQUA + String.format("k/d: %s", "None"));
        } else {
            p.sendMessage(ChatColor.AQUA + String.format("k/d: %s", String.valueOf(players.getConfig().getInt(String.valueOf(p.getUniqueId()) + ".kill") / players.getConfig().getInt(String.valueOf(p.getUniqueId()) + ".death"))));

        }
    }

    void Stop_battle(Player p) {
        battle.preset_battle = false;
        p.sendMessage("停止");
    }

}

