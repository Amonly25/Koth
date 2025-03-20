package com.ar.askgaming.koth.Misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;
import com.ar.askgaming.koth.Controllers.KothManager.KothRadius;

public class ScoreBoardUtil extends BukkitRunnable{
    
    private ScoreboardManager manager;
    private Scoreboard board;

    private boolean enabled = true;
    private String title;
    private String king;
    private String countdown;
    private String loc;
    private String type;

    private KothPlugin plugin;
    public ScoreBoardUtil(KothPlugin main) {
        plugin = main;

        setupScoreboard();
        this.runTaskTimer(plugin, 20, 20);

        loadConfig();
    }
    private void loadConfig() {
        enabled = plugin.getConfig().getBoolean("scoreboard.enabled", true);
        title = plugin.getConfig().getString("scoreboard.title", "King of the Hill").replace('&', '§');
        type = plugin.getConfig().getString("scoreboard.type", "Type: %type%").replace('&', '§');
        king = plugin.getConfig().getString("scoreboard.king", "King: %player%").replace('&', '§');
        countdown = plugin.getConfig().getString("scoreboard.countdown", "Time left: %time%").replace('&', '§');
        loc = plugin.getConfig().getString("scoreboard.loc", "X: %x% Y: %y%").replace('&', '§');
    }

    public void setupScoreboard() {
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();

        // Crear un objetivo en el scoreboard
        Objective obj = board.registerNewObjective("server", Criteria.DUMMY, title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score text = obj.getScore(ChatColor.DARK_RED + "");
        text.setScore(8);

        Team type = board.registerNewTeam("type");
        type.addEntry(ChatColor.BLACK + "");
        obj.getScore(ChatColor.BLACK + "").setScore(7);

        Score text1 = obj.getScore(ChatColor.BLACK + "");
        text1.setScore(6);

        Team king = board.registerNewTeam("king");
		king.addEntry(ChatColor.AQUA + "");
		obj.getScore(ChatColor.AQUA + "").setScore(5);

        Score text2 = obj.getScore("");
		text2.setScore(4);

        Team countdown = board.registerNewTeam("countdown");
		countdown.addEntry(ChatColor.RED+ "");
		obj.getScore(ChatColor.RED+ "").setScore(3);

        Score text3 = obj.getScore(ChatColor.WHITE + "");
		text3.setScore(2);

        Team loc = board.registerNewTeam("loc");
		loc.addEntry(ChatColor.GRAY+ "");
		obj.getScore(ChatColor.GRAY+ "").setScore(1);
		
    }

    public void updateScoreBoard() {
    
        Koth koth = plugin.getManager().getActiveKoth();
        if (koth == null) {
            return;
        }
        Player king = koth.getKing();

        String king_name = king != null ? king.getName() : "None";

        board.getTeam("type").setPrefix(type.replace("%type%", koth.getType().toString()));

        board.getTeam("king").setPrefix(this.king.replace("%player%", king_name));

        board.getTeam("countdown").setPrefix(countdown.replace("%time%", plugin.getManager().getCountdownText(koth)));

        String x,y;
        Location l;
        if (koth.getKothRadius() == KothRadius.CIRCLE) {
            l = koth.getLoc();
        } else {
            l = koth.getBlock1();
        }
        if (l == null) {
            return;
        }
        x = String.valueOf(l.getBlockX());
        y = String.valueOf(l.getBlockY());

        board.getTeam("loc").setPrefix(loc.replace("%x%",x).replace("%y%", y));
    }

    public Scoreboard getScoreboard() {
        return board;
    }

    @Override
    public void run() {
        if (!enabled) {
            return;
        }
        Koth koth = plugin.getManager().getActiveKoth();
        if (koth == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getScoreboard() == getScoreboard()) {
                    p.setScoreboard(manager.getNewScoreboard());
                }
            }
            return;
        }
        updateScoreBoard();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(getScoreboard());
        }
    }  
}
