package com.ar.askgaming.koth.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;
import com.ar.askgaming.koth.Controllers.KothManager.KothState;


public class PlayerMoveListener implements Listener{
    
    private KothPlugin plugin;
    public PlayerMoveListener(KothPlugin main) {
        plugin = main;
    }

    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        Location location = player.getLocation();

        Koth koth = plugin.getManager().getKothByLocation(location);

        if (koth != null) {
            if (koth.getState() != KothState.INPROGRESS) {
                return;
            }
            plugin.getManager().addPlayerToKoth(player, koth);
        }
    }
}
