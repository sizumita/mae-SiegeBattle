package mae.siegebattle.manager;

import lombok.Getter;
import mae.siegebattle.Config;
import mae.siegebattle.DiscordHook;
import mae.siegebattle.SiegeBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class scoreboardManager {
    @Getter
    private final SiegeBattle plugin;
    private DiscordHook hook = new DiscordHook();
    public Config config;

    public scoreboardManager(SiegeBattle plugin){
        this.plugin = plugin;
        this.hook.setWebhook(plugin.webhook);
        this.config = new Config(plugin);
    }

    public void reloadScoreBoard(Player player){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("score board","dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§e§l§kaaa§r§e§lMAE Server§kaaa");
        Score prefix = obj.getScore("§l " + config.get_prefix(player, true));
        Score name = obj.getScore("§e§l " + player.getName());
        Score rank = obj.getScore("§l rank: " + String.valueOf(config.get_rank(player)));
        Score kill = obj.getScore("§l kill: " + String.valueOf(config.get_kill(player)));
        Score death = obj.getScore("§l death: " + String.valueOf(config.get_death(player)));
        Score money = obj.getScore("§l 所持金: " + String.valueOf(config.get_money(player) + "円"));
        Score kd = obj.getScore("§l k/d: " + config.get_kd(player));
        Score is_battle;
        Score king_blue;
        Score king_red;
        Score time;
        if(plugin.battle.start_battle){
            is_battle = obj.getScore("§l バトル: " + "開始中！");
            if(plugin.battle.starting_battle){
                king_blue = obj.getScore("§l 青の大将: " + plugin.battle.blue_king.getName());
                king_red = obj.getScore("§l 赤の大将: " + plugin.battle.red_king.getName());
                int min = (plugin.battle.time % 3600) / 60;
                int sec = plugin.battle.time % 60;
                time = obj.getScore("§l§b 残り時間:" + String.valueOf(min) + "分" + String.valueOf(sec) + "秒");
            } else {
                king_blue = obj.getScore("§l 青の大将: " + "-----");
                king_red = obj.getScore("§l 赤の大将: " + "-----");
                time = obj.getScore("§b§l 残り時間:" + "-----");
            }
        } else {
            is_battle = obj.getScore("§l バトル: " + "行われていません。");
            king_blue = obj.getScore("§l 青の大将: " + "-----");
            king_red = obj.getScore("§l 赤の大将: " + "-----");
            time = obj.getScore("§l§b 残り時間:" + "-----");
        }
        prefix.setScore(10);
        name.setScore(9);
        rank.setScore(8);
        kill.setScore(7);
        death.setScore(6);
        money.setScore(5);
        kd.setScore(4);
        is_battle.setScore(3);
        king_blue.setScore(2);
        king_red.setScore(1);
        time.setScore(0);
        player.setScoreboard(board);
    }
}
