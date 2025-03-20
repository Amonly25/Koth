package com.ar.askgaming.koth;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.koth.Controllers.KothManager;
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

    public void onEnable() {

        saveDefaultConfig();

        ConfigurationSerialization.registerClass(Koth.class,"Koth");
        
        manager = new KothManager(this);
        langManager = new LangManager(this);
        myScoreBoard = new ScoreBoardUtil(this);

        new PlayerMoveListener(this);
        new PlayerInteractListener(this);
        new PlayerJoinListener(this);

        new Commands(this);
    }   

    public void onDisable() {
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
}