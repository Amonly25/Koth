package com.ar.askgaming.koth.Listeners;

import java.net.http.WebSocket.Listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;

public class PlayerInteractListener implements Listener, org.bukkit.event.Listener{
    
    private KothPlugin plugin;
    public PlayerInteractListener(KothPlugin main) {
        plugin = main;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent e){

        Player p = (Player) e.getPlayer();
        Koth koth = plugin.getManager().getEditingKoths().get(p);
        if (koth != null){
            e.setCancelled(true);
            Block block = e.getClickedBlock();
            
            switch (e.getAction()) {
                case LEFT_CLICK_BLOCK:
                    koth.setBlock1(block.getLocation());
                    p.sendMessage("§aBlock 1 set for koth " + koth.getName());
                    plugin.getManager().saveKoths();
                    plugin.getManager().getEditingKoths().remove(p);
                    break;
                case RIGHT_CLICK_BLOCK:
                    koth.setBlock2(block.getLocation());
                    p.sendMessage("§aBlock 2 set for koth " + koth.getName());
                    plugin.getManager().getEditingKoths().remove(p);
                    plugin.getManager().saveKoths();
                    break;
                default:
                    break;
            }
        }
    }
}
