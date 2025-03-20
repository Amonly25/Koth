package com.ar.askgaming.koth.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;

public class PlayerJoinListener implements Listener{

    private KothPlugin plugin;
    public PlayerJoinListener(KothPlugin main) {
        plugin = main;
    }
    
    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        // Check if the player is in the game
        Koth koth = plugin.getManager().getActiveKoth();
        if (koth != null) {
            p.setScoreboard(plugin.getMyScoreBoard().getScoreboard());
        }    
    }
}
