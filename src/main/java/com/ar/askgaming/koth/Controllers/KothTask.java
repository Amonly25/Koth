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

import com.ar.askgaming.betterclans.Clan.Clan;
import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;
import com.ar.askgaming.koth.Controllers.KothManager.KothState;
import com.ar.askgaming.koth.Controllers.KothManager.KothType;

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
        if (koths.isEmpty()) return;
    
        checkTimeForScheduler();
    
        for (Koth koth : koths) {
            if (koth.getState() != KothState.INPROGRESS) continue;
    
            List<Player> players = manager.getPlayersInKoth(koth);
    
            switch (koth.getMode()) {
                case BY_TIME:
                    if (players.isEmpty()){
                        koth.setKing(null);
                        koth.setCountdown(koth.getDuration());
                        return;
                    }
                    handleByTimeMode(koth, players);
                    break;
    
                case CONTROL:
                    if (players.isEmpty()){
                        koth.setKing(null);
                        koth.setCountdown(koth.getDuration());
                        return;
                    }
                    handleByControlMode(koth, players);
                    break;
    
                case CAPTURE:
                    if (players.isEmpty()){
                        return;
                    }
                    koth.updateBossBar();
                    BossBar bossBar = koth.getBossBar();
            
                    for (Player player : players) {
                        if (!bossBar.getPlayers().contains(player)) {
                            bossBar.addPlayer(player);
                        }
                    }
                    if (players.isEmpty() && koth.getKing() == null){
                        return;
                    }
                    handleCaptureMode(koth, players);
                    break;
    
                default:
                    break;
            }
            int countdown = koth.getCountdown();

            if (countdown > 0) {
                koth.setCountdown(countdown - 1);
            } else {
                plugin.getManager().endKoth(koth);
            }
        }
    }
    //#region controlmode
    private void handleByControlMode(Koth koth, List<Player> players) {

        Player king = koth.getKing();
        if (king != null && players.contains(king)) {
            return;
        }
        assignNewKing(koth, players); 

    }
    //#region bytimemode
    private void handleByTimeMode(Koth koth, List<Player> players) {
        HashMap<Player, Integer> controlTime = koth.getPlayersControlTime();

        Player highestPlayer = null;
        int highestTime = -1;
    
        for (Player player : players) {
            int time = controlTime.getOrDefault(player, 0) + 1;
            controlTime.put(player, time);
    
            if (time > highestTime) {
                highestTime = time;
                highestPlayer = player;
            }
        }
    
        koth.setKing(highestPlayer);
    }
    //#region capturemode
    private void handleCaptureMode(Koth koth, List<Player> players) {

        if (players.isEmpty()) return;

        Player king = koth.getKing();

        if (koth.getType() == KothType.CLAN) {
            if (plugin.getClansInstance() == null){
                plugin.getLogger().severe("A koht is set to CLAN mode but Clans plugin is not found");
                handleSoloCapture(koth, king, players);
                return;
            }
            handleClanCapture(koth, king, players);
        } else {
            handleSoloCapture(koth, king, players);
        }
    }

    /**
     * Maneja la lógica de captura en el modo CAPTURE para clanes.
     */
    private void handleClanCapture(Koth koth, Player king, List<Player> players) {

        if (king == null || !arePlayerWithSameClan(koth, players)) {
            int random = (int) (Math.random() * players.size());
            Player newKing = players.get(random);
            Clan playerClan = plugin.getClansInstance().getClansManager().getClanByPlayer(newKing);
            if (playerClan == null) {
                newKing.sendMessage("§cYou must be in a clan to capture this KOTH");
                players.remove(newKing);
                return;
            }
            koth.setClan(playerClan);
            assignNewKing(koth, players);
        }
    }
    private boolean arePlayerWithSameClan(Koth koth, List<Player> players) {
        Clan kothClan = koth.getClan();
        for (Player player : players) {
            Clan playerClan = plugin.getClansInstance().getClansManager().getClanByPlayer(player);
            if (playerClan != null && playerClan.equals(kothClan)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Maneja la lógica de captura en el modo CAPTURE para jugadores individuales.
     */
    private void handleSoloCapture(Koth koth, Player king, List<Player> players) {
        if (king != null && manager.isInsideKoth(koth, king.getLocation())) {
            if (koth.getCountdown() >= koth.getDuration()) {
                plugin.getManager().endKoth(koth);
            }
        } else {
            assignNewKing(koth, players);
        }
    }

    /**
     * Asigna un nuevo "rey" (king) y resetea la cuenta regresiva.
     */
    private void assignNewKing(Koth koth, List<Player> players) {
        Player newKing = players.get(0);
        koth.setCountdown(koth.getDuration());
        koth.setKing(newKing);
    }
    //#region checktime
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
                    if (koth != null && koth.getState() == KothState.WAITING_NEXT) {
                        plugin.getManager().startKoth(koth);
                    }
                }
            }
        }
    }
}
