package com.ar.askgaming.koth.Controllers;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;

public class KothTask extends BukkitRunnable{

    private KothManager manager;
    private KothPlugin plugin;
    public KothTask(KothPlugin main, KothManager manager) {
        plugin = main;
        this.manager = manager;

        this.runTaskTimer(plugin, 20, 20);
    }

    @Override
    public void run() {
        List<Koth> koths = manager.getKoths();
        if (koths.isEmpty()) {return;}
        for (Koth koth : koths) {
            if (koth.getState() != KothManager.KothState.INPROGRESS) {continue;}
            int countdown = koth.getCountdown();

            if (countdown > 0) {
                koth.setCountdown(countdown - 1);
            } else {
                plugin.getManager().endKoth(koth);
            }
        }
    }
}
