package mae.siegebattle;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.*;

public class Battle_control {
    @Getter
    private final SiegeBattle plugin;
    DiscordHook hook = new DiscordHook();
    Config config;
    public Boolean start_battle = false;
    public Boolean waiting = false;
    public Boolean starting_battle = false;
    public Boolean preset_battle = true;
    public ArrayList<String> battle_list = new ArrayList<String>();
    public ArrayList<Player> blue = new ArrayList<>();
    public ArrayList<Player> canBuild = new ArrayList<>();
    public ArrayList<Player> red = new ArrayList<>();
    public Integer map_red_x;
    public Integer map_red_y;
    public Integer map_red_z;
    public Integer map_blue_x;
    public Integer map_blue_y;
    public Integer map_blue_z;
    public String map_name;
    public Integer red_resoult_kill = 0;
    public Integer blue_resoult_kill = 0;
    public int time = 300;
    public Map<Player, Integer> kill_num = new HashMap<>();
    public Player blue_king;
    public Player red_king;
    private CustomConfig field;
    Random rand = new Random();
    Boolean test = true;
    int battle_id = 0;


    public Battle_control(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.config = new Config(plugin);
        this.field = new CustomConfig(plugin, "field.yml");
        battle_list.add("砂の惑星 Sand planet");
        battle_list.add("王国 Kingdom");
        battle_list.add("空島 Sky Island");
        battle_list.add("オフィス街 office Street");

    }

    public void Start_battle() {
        if (start_battle) {
            return;
        }
        start_battle = true;
        for (Player s : plugin.getServer().getOnlinePlayers()){
            plugin.TitleSender.sendTitle(s, "募集が開始されました", "20秒後に募集終了します", "そのままにしていれば参加可能です。");
        }
        if (plugin.getServer().getOnlinePlayers().size() >= 2) {
            Bukkit.broadcastMessage(ChatColor.AQUA + String.format("バトルを開始します。参加人数%d人", plugin.getServer().getOnlinePlayers().size()));
            battle();
            return;
        }
        Bukkit.broadcastMessage(ChatColor.AQUA + "参加者を募集しています...しばらくお待ちください...");
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                //Your code here!

                if (plugin.getServer().getOnlinePlayers().size() < 2) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + "参加人数が足りず、募集を停止しました。");
                    start_battle = false;
                    return;
                } else if(preset_battle){
                    Bukkit.broadcastMessage(ChatColor.AQUA + String.format("バトルを開始します。参加人数%d人", plugin.getServer().getOnlinePlayers().size()));
                    battle();
                    return;
                }
            }
        }, 200);

    }

    public void finish_battle(String win){
        ArrayList<Player> win_team;
        if(win.equals("red")) {
            win_team = red;
        } else {
            win_team = blue;
        }
        if(test){
            if(win.equals("red")){
                starting_battle = false;
                for (Player s : plugin.getServer().getOnlinePlayers()){
                    plugin.TitleSender.sendTitle(s, "赤チームの勝ち！", "赤チームに報酬が配られます..", "");
                }
//                for (Player s: red){
//                    Integer money_before = players.getConfig().getInt(String.valueOf(s.getUniqueId()) + ".money");
//                    players.getConfig().set(String.valueOf(s.getUniqueId()) + ".money", money_before + 500);
//                    s.sendMessage("§6あなたは500円ゲットした！");
//                }

//                for (Player s: blue){
//                    s.sendMessage(ChatColor.RED + "何も獲得できなかった....");
//                }

            } else if(win.equals("blue")){
                starting_battle = false;
                for (Player s : plugin.getServer().getOnlinePlayers()){
                    plugin.TitleSender.sendTitle(s, "青チームの勝ち！", "青チームに報酬が配られます..", "");
                }
//                for (Player s: blue){
//                    Integer money_before = players.getConfig().getInt(String.valueOf(s.getUniqueId()) + ".money");
//                    players.getConfig().set(String.valueOf(s.getUniqueId()) + ".money", money_before + 500);
//                    s.sendMessage("§6あなたは500円ゲットした！");
//                }

//                for (Player s: red){
//                    s.sendMessage(ChatColor.RED + "何も獲得できなかった....");
//                }
            }
            String name = "いませんでした";
            int kill_var = 0;
            for(Map.Entry<Player, Integer> entry: kill_num.entrySet()){
                if(win_team.contains(entry.getKey())){
                    entry.getKey().sendMessage("§6§lあなたは" + String.valueOf(entry.getValue() * 20) + "円ゲットした！");
                    config.add_money(entry.getKey(), entry.getValue() * 20);
                } else {
                    entry.getKey().sendMessage(ChatColor.RED + "何も獲得できなかった....");
                }
                if(kill_var < entry.getValue()) name = entry.getKey().getPlayerListName(); kill_var = entry.getValue();
            }
            String resalt = "§a----- §f§l[バトルリザルト] §a-----§l§e\n";
            resalt +=       "              合計キル数             \n";
            resalt +=       "               赤:" + String.valueOf(red_resoult_kill) + "\n";
            resalt +=       "               青:" + String.valueOf(blue_resoult_kill) + "\n";
            resalt +=       "             最多キル数のプレイヤー\n";
            resalt +=       "              " + name + "\n";
            resalt +=       "              キル数:" + String.valueOf(kill_var) + " キル\n";
            Bukkit.broadcastMessage(resalt);
            hook.sendSystemMessage("**バトルが終了しました！**");
            hook.sendSystemMessage(resalt);
            End_battle();
        } else {
            if(win.equals("blue")){
                for (Player s : plugin.getServer().getOnlinePlayers()){
                    plugin.TitleSender.sendTitle(s, "§9青チームの勝ち！", "", "");
                }
            } else {
                for (Player s : plugin.getServer().getOnlinePlayers()){
                    plugin.TitleSender.sendTitle(s, "§c赤チームの勝ち！", "", "");
                }
            }
            End_battle();

        }
    }

    public void battle(){
        Bukkit.broadcastMessage(ChatColor.AQUA + "参加者を赤チームと青チームに振り分けます...しばらくお待ちください...");
        ArrayList<Player> setplayers = new ArrayList<>();
        setplayers.addAll(plugin.getServer().getOnlinePlayers());
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
                s.sendMessage("§l§eあなたは§c赤チーム§eに配属されました！");
                plugin.TitleSender.sendTitle(s, "", "§l§eあなたは§c赤チーム§eに配属されました！", "");
                s.setPlayerListName(config.get_prefix(s, true) + s.getName());
                s.getInventory().clear();
                config.kit_load(s, "red");
                s.setGameMode(GameMode.SPECTATOR);
            } else {
                blue.add(s);
                s.sendMessage("§l§eあなたは§9青チーム§eに配属されました！");
                plugin.TitleSender.sendTitle(s, "", "§l§eあなたは§9青チーム§eに配属されました！", "");
                s.setPlayerListName(config.get_prefix(s, true) + s.getName() + s.getName());
                s.getInventory().clear();
                config.kit_load(s, "blue");
                s.setGameMode(GameMode.SPECTATOR);
            }

        }
        Collections.shuffle(battle_list);
        for (Player s : plugin.getServer().getOnlinePlayers()){
            plugin.TitleSender.sendTitle(s, battle_list.get(0), "10秒後にゲームを開始します", "");
        }
        int num = rand.nextInt(blue.size());
        blue_king = blue.get(num);
        plugin.TitleSender.sendTitle(blue_king, "", "あなたは青チームの大将になりました！", "");
        Bukkit.broadcastMessage("§l§enameさんが青の大将になりました！".replace("name", blue_king.getName()));
        num = rand.nextInt(red.size());
        red_king = red.get(num);
        plugin.TitleSender.sendTitle(red_king, "", "あなたは赤チームの大将になりました！", "");
        Bukkit.broadcastMessage("§l§enameさんが赤の大将になりました！".replace("name", red_king.getName()));
        config.set_all_member_list_prefix();
//        battle_list.add("砂の惑星 Sand planet");
//        battle_list.add("王国 Kingdom");
//        battle_list.add("空島 Sky Island\n");
//        battle_list.add("オフィス街 office Street\n");

//        test = trueの時　テストワールドに直行 new SelfCancelingTask(this, 5).runTaskTimer(this, 10, 20);
        plugin.getServer().createBossBar("青の大将のHP", BarColor.BLUE, BarStyle.SOLID);
        if(!test){
            if (battle_list.get(0).equals("砂の惑星 Sand planet")){
                map_red_x = field.getConfig().getInt("fields.1.red.x");
                map_red_y = field.getConfig().getInt("fields.1.red.y");
                map_red_z = field.getConfig().getInt("fields.1.red.z");
                map_blue_x = field.getConfig().getInt("fields.1.blue.x");
                map_blue_y = field.getConfig().getInt("fields.1.blue.y");
                map_blue_z = field.getConfig().getInt("fields.1.blue.z");
                map_name = "砂の惑星 Sand planet";
            } else if (battle_list.get(0).equals("王国 Kingdom")){
                map_red_x = field.getConfig().getInt("fields.2.red.x");
                map_red_y = field.getConfig().getInt("fields.2.red.y");
                map_red_z = field.getConfig().getInt("fields.2.red.z");
                map_blue_x = field.getConfig().getInt("fields.2.blue.x");
                map_blue_y = field.getConfig().getInt("fields.2.blue.y");
                map_blue_z = field.getConfig().getInt("fields.2.blue.z");
                map_name = "王国 Kingdom";
            } else if (battle_list.get(0).equals("空島 Sky Island")){
                map_red_x = field.getConfig().getInt("fields.3.red.x");
                map_red_y = field.getConfig().getInt("fields.3.red.y");
                map_red_z = field.getConfig().getInt("fields.3.red.z");
                map_blue_x = field.getConfig().getInt("fields.3.blue.x");
                map_blue_y = field.getConfig().getInt("fields.3.blue.y");
                map_blue_z = field.getConfig().getInt("fields.3.blue.z");
                map_name = "空島 Sky Island";
            } else if (battle_list.get(0).equals("オフィス街 office Street")){
                map_red_x = field.getConfig().getInt("fields.4.red.x");
                map_red_y = field.getConfig().getInt("fields.4.red.y");
                map_red_z = field.getConfig().getInt("fields.4.red.z");
                map_blue_x = field.getConfig().getInt("fields.4.blue.x");
                map_blue_y = field.getConfig().getInt("fields.4.blue.y");
                map_blue_z = field.getConfig().getInt("fields.4.blue.z");
                map_name = "オフィス街 office Street";
            }
        } else {
            map_red_x = field.getConfig().getInt("fields.test.red.x");
            map_red_y = field.getConfig().getInt("fields.test.red.y");
            map_red_z = field.getConfig().getInt("fields.test.red.z");
            map_blue_x = field.getConfig().getInt("fields.test.blue.x");
            map_blue_y = field.getConfig().getInt("fields.test.blue.y");
            map_blue_z = field.getConfig().getInt("fields.test.blue.z");
            map_name = "テストワールド test world";
        }

//        ここで移動させて、waiting true
        Bukkit.broadcastMessage(ChatColor.AQUA + "10秒後にゲームを開始します..しばらくお待ちください。");
        for(Player s: red){
            s.teleport(new Location(plugin.getServer().getWorld("battle"), map_red_x, map_red_y, map_red_z));
            s.setHealth(20);

        }
        for(Player s: blue){
            s.teleport(new Location(plugin.getServer().getWorld("battle"), map_blue_x, map_blue_y, map_blue_z));
            s.setHealth(20);


        }
        String chattext = "**バトルが始まりました！**\n青の大将:**{0}**\n赤の大将:**{1}**"
                .replace("{0}", blue_king.getPlayerListName()
                        .replace("{1}", red_king.getPlayerListName()));
        hook.sendSystemMessage(chattext);
        waiting = true;
        int uniuqe_id = battle_id;
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                //Your code here!
                for (Player s : plugin.getServer().getOnlinePlayers()){
                    plugin.TitleSender.sendTitle(s, "バトル開始！", "青チームは五分耐久だ！", "赤チームは青チームの大将を倒せば勝ちだ！");
                    plugin.manager.reloadScoreBoard(s);
                    s.setGameMode(GameMode.SURVIVAL);
                    s.setFoodLevel(20);
                    s.setHealth(20);
                }
                waiting = false;
                starting_battle = true;


            }
        }, 200);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

            @Override
            public void run() {
                if(uniuqe_id == battle_id) {
                    finish_battle("blue");
                }
            }

        }, 300 * 20L);
        new SelfCancelingTask(plugin, 300, battle_id).runTaskTimer(plugin, 0, 20);
    }

    public boolean End_battle() {
        Bukkit.broadcastMessage("変数を初期化しています....");
        battle_id += 1;
        time = 300;
        preset_battle = false;
        start_battle = false;
        waiting = false;
        starting_battle = false;
        preset_battle = true;
        blue = new ArrayList<>();
        red = new ArrayList<>();
        blue_king = null;
        red_king = null;
        red_resoult_kill = 0;
        blue_resoult_kill = 0;
        kill_num = new HashMap<>();
        for (Player s : plugin.getServer().getOnlinePlayers()){
            s.getInventory().clear();
            s.setGameMode(GameMode.SURVIVAL);
            s.teleport(plugin.spawnLocation);
            s.setPlayerListName(config.get_prefix(s, true) + s.getName());
            s.setFoodLevel(20);
            plugin.manager.reloadScoreBoard(s);
        }
        Bukkit.broadcastMessage("完了");
        Bukkit.broadcastMessage("configを再ロードしています....");
        field.reloadConfig();
        plugin.reloadConfig();
        plugin.players.reloadConfig();
        plugin.kit.reloadConfig();
        Bukkit.broadcastMessage("完了");
        config.reload();
        plugin.manager.config.reload();
        return true;
    }


}
