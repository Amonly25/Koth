package com.ar.askgaming.koth.Controllers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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

        checkTimeForScheduler();

        for (Koth koth : koths) {
            if (koth.getState() != KothManager.KothState.INPROGRESS) {continue;}
            int countdown = koth.getCountdown();
            HashMap<Player, Integer> controlTime = koth.getPlayersControlTime();
            List<Player> players = manager.getPlayersInKoth(koth);
            switch (koth.getMode()) {
                case BY_TIME:
                    if (!players.isEmpty()) {
                        for (Player player : players) {
                            controlTime.put(player, controlTime.getOrDefault(player, 0) + 1);
                        }
                        // Determinar el jugador con más tiempo
                        Player highestPlayer = players.get(0);
                        int highestTime = controlTime.get(highestPlayer);
                        for (Player player : players) {
                            int seconds = controlTime.get(player);
                            if (seconds > highestTime) {
                                highestTime = seconds;
                                highestPlayer = player;
                            }
                        }
                        koth.setKing(highestPlayer);
                    } else{
                        koth.setKing(null);
                        koth.getPlayersControlTime().clear();
                    }
                    break;
                case CONTROL:
                    if (players.isEmpty()) {
                        koth.setKing(null);
                        break;
                    }
                    break;
                case CAPTURE:
                    koth.updateBossBar();
                    BossBar bossBar = koth.getBossBar();
                    for (Player player : players) {
                        if (!bossBar.getPlayers().contains(player)) {
                            bossBar.addPlayer(player);
                        }
                    }
                    if (!players.isEmpty()) {
                        Player king = koth.getKing();
                        if (king != null && manager.isInsideKoth(koth, king.getLocation())) {

                            if (koth.getCountdown() >= koth.getDuration()) {
                                plugin.getManager().endKoth(koth);
                                return; // Evita que el countdown continúe si el KOTH terminó
                            }
                        } else {
                            // Determinar un nuevo rey con más tiempo controlando
                            Player newKing = players.get(0);
                            koth.setCountdown(koth.getDuration());
                            koth.setKing(newKing);
                        }
                    } else{
                        koth.setKing(null);
                        koth.setCountdown(koth.getDuration());
                        return;
                    }
                default:
                    break;
            }
            if (countdown > 0) {
                koth.setCountdown(countdown - 1);
            } else {
                plugin.getManager().endKoth(koth);
            }
        }
    }
    private void checkTimeForScheduler() {

        ConfigurationSection scheduler = plugin.getConfig().getConfigurationSection("scheduler");
        if (scheduler == null) {
            plugin.getLogger().severe("No scheduler section found in config");
            return;
        }

        String currentDay = java.time.LocalDate.now().getDayOfWeek().name().toLowerCase();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        ConfigurationSection daySection = scheduler.getConfigurationSection(currentDay);
        if (daySection != null) {
            Set<String> times = daySection.getKeys(false);
            for (String time : times) {
                if (time.equals(currentTime)) {
                    String kothName = daySection.getString(time);
                    if (kothName == null || kothName.isEmpty()) {
                        continue;
                    }
                    Koth koth = plugin.getManager().getByName(kothName);
                    if (koth != null) {
                        plugin.getManager().startKoth(koth);
                    }
                }
            }
        }
    }
}
