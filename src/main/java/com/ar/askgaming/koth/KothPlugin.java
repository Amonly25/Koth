package com.ar.askgaming.koth;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.betterclans.BetterClans;
import com.ar.askgaming.koth.Controllers.KothManager;
import com.ar.askgaming.koth.Controllers.KothManager.KothState;
import com.ar.askgaming.koth.Controllers.LangManager;
import com.ar.askgaming.koth.Listeners.PlayerInteractListener;
import com.ar.askgaming.koth.Listeners.PlayerJoinListener;
import com.ar.askgaming.koth.Listeners.PlayerMoveListener;
import com.ar.askgaming.koth.Misc.Commands;
import com.ar.askgaming.koth.Misc.ScoreBoardUtil;

public class KothPlugin extends JavaPlugin {

    private ScoreBoardUtil myScoreBoard;
    private KothManager manager;
    private LangManager langManager;
    private static KothPlugin instance;
    private BetterClans clansInstance;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        ConfigurationSerialization.registerClass(Koth.class,"Koth");
        
        manager = new KothManager(this);
        langManager = new LangManager(this);
        myScoreBoard = new ScoreBoardUtil(this);

        new PlayerMoveListener(this);
        new PlayerInteractListener(this);
        new PlayerJoinListener(this);

        new Commands(this);

        if (getServer().getPluginManager().getPlugin("BetterClans") != null) {
            clansInstance = BetterClans.getInstance();

        }
    }   

    public void onDisable() {
        for (Koth koth : manager.getKoths()) {
            if (koth.getState() == KothState.INPROGRESS) {
                getManager().stopKoth(koth);
            }
        }
        for (Player player : getServer().getOnlinePlayers()) {
            player.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
        }

    }
    public static KothPlugin getInstance() {
        return instance;
    }
    public ScoreBoardUtil getMyScoreBoard() {
        return myScoreBoard;
    }
    public KothManager getManager() {
        return manager;
    }
    public LangManager getLangManager() {
        return langManager;
    }
    public BetterClans getClansInstance() {
        return clansInstance;
    }
}