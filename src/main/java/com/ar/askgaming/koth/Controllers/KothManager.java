package com.ar.askgaming.koth.Controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.betterclans.BetterClans;
import com.ar.askgaming.betterclans.Clan.Clan;
import com.ar.askgaming.betterclans.Managers.ClansManager;
import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;
import com.ar.askgaming.koth.Events.KothEndEvent;
import com.ar.askgaming.koth.Events.KothStartEvent;
import com.ar.askgaming.koth.Misc.ParticleTask;
import com.ar.askgaming.universalnotifier.UniversalNotifier;
import com.ar.askgaming.universalnotifier.Managers.AlertManager.Alert;

public class KothManager {

    private File kothFile;
    private FileConfiguration kothConfig;

    private KothPlugin plugin;
    public KothManager(KothPlugin main) {
        plugin = main;

        new ParticleTask(plugin, this);
        new KothTask(plugin, this);

        loadKoths();
    }

    private List<Koth> koths = new ArrayList<>();
    private HashMap<Player, Koth> editingKoths = new HashMap<>();
    public HashMap<Player, Koth> getEditingKoths() {
        return editingKoths;
    }
    public List<Koth> getKoths() {
        return koths;
    }
    public enum KothRadius {
        CIRCLE, SQUARE
    }
    public enum KothState {
        WAITING_NEXT, INPROGRESS
    }
    public enum KothType {
        SOLO, CLAN
    }
    public enum KothMode {
        BY_TIME,
        CONTROL,
        CAPTURE
    }
    private String getLang(String path, Player player) {
        return plugin.getLangManager().getFrom(path, player);
    }

    private void loadKoths() {
        kothFile = new File(plugin.getDataFolder(), "koths.yml");
        if (!kothFile.exists()) {
            plugin.saveResource("koths.yml", false);
        }
        kothConfig = YamlConfiguration.loadConfiguration(kothFile);

        Set<String> kothKeys = kothConfig.getKeys(false);
        if (kothKeys == null) {
            return;
        }
        for (String key : kothKeys) {
            Object obj = kothConfig.get(key);
            if (obj instanceof Koth) {
                koths.add((Koth) obj);
            }
        }
    }
    //#region create
    public void createKoth(String name, KothType type, KothMode mode, KothRadius radius, Integer time) {
        Koth koth = new Koth(name, type, mode, radius, time);
        koths.add(koth);
        saveKoths();
    }
    //#region start
    public void startKoth(Koth koth) {
        if (Bukkit.getOnlinePlayers().size() < koth.getMinimunPlayers()){
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(getLang("not-enough-players", player));
            }
            plugin.getLogger().warning("Not enough players to start the koth");
            return;
        }
        koth.setState(KothState.INPROGRESS);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(getLang("start", player).replace("{name}", koth.getName()));
        }
        if (plugin.getServer().getPluginManager().getPlugin("UniversalNotifier") != null) {
            UniversalNotifier notifier = UniversalNotifier.getInstance();
            String message = plugin.getConfig().getString("notifier.start").replace("%name%", koth.getName()).replace("%mode%", koth.getMode().toString());
            notifier.getNotificationManager().broadcastToAll(Alert.CUSTOM, message);
        }

        Bukkit.getPluginManager().callEvent(new KothStartEvent(koth));
    }
    //#region delete
    public void deleteKoth(Koth koth) {
        koths.remove(koth);
        kothConfig.set(koth.getName(), null);
        saveKoths();
    }
    //#region end
    public void endKoth(Koth koth) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(getLang("end", player).replace("{name}", koth.getName()));
        }
        Player king = koth.getKing();
        if (king == null){
            koth.reset();
            return;
        }
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(getLang("new_king", player).replace("{player}", king.getName()).replace("{name}", koth.getName()));
        }
        List<Player> players = new ArrayList<>();
        players.add(king);
        
        //Clan
        if (koth.getType() == KothType.CLAN) {
            if (plugin.getServer().getPluginManager().getPlugin("BetterClans") != null) {
                BetterClans clans = BetterClans.getInstance();
                ClansManager clansManager = clans.getClansManager();
                Clan clan = clansManager.getClanByPlayer(king);
                if (clan != null) {
                    for (Player player : clansManager.getAllClanMembers(clan)) {
                        if (!players.contains(player)) {
                            players.add(player);
                        }
                    }
                    if (koth.getMode() == KothMode.CAPTURE) {
                        
                    }
                    // Do something with the clan
                }
            } else{
                plugin.getLogger().warning("BetterClans is not installed!");
            }
        }
        List<String> rewards = plugin.getConfig().getStringList("rewards."+koth.getName());
        for (Player player : players) {
            player.sendMessage(getLang("reward", player).replace("{name}", koth.getName()));
            
            for (String command : rewards) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
            }
        }
        if (plugin.getServer().getPluginManager().getPlugin("UniversalNotifier") != null) {
            UniversalNotifier notifier = UniversalNotifier.getInstance();
            String message = plugin.getConfig().getString("notifier.end","").replace("%player%", king.getName()).replace("%name%", koth.getName());
            notifier.getNotificationManager().broadcastToAll(Alert.CUSTOM, message);
        }
        Bukkit.getPluginManager().callEvent(new KothEndEvent(koth));
        koth.reset();
    }
    //#region stop
    public void stopKoth(Koth koth) {
        koth.reset();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(getLang("stop", player).replace("{name}", koth.getName()));
        }
    }
    //#region save
    public void saveKoths() {
        for (Koth koth : koths) {
            kothConfig.set(koth.getName(), koth);
        }
        try {
            kothConfig.save(kothFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //#region getKoth
    public Koth getKothByLocation(Location loc) {
        for (Koth koth : koths) {
            if (isInsideKoth(koth, loc)) {
                return koth;
            }
        }
        return null;
    }
    public Koth getByName(String name) {
        for (Koth koth : koths) {
            if (koth.getName().equalsIgnoreCase(name)) {
                return koth;
            }
        }
        return null;
    }
    public Koth getActiveKoth() {
        for (Koth koth : koths) {
            if (koth.getState() == KothState.INPROGRESS) {
                return koth;
            }
        }
        return null;
    }
    public String getCountdownText(Koth koth) {
        Integer seconds = koth.getCountdown();
        if (seconds == 0) {
            return "00:00";
        }
        Integer minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    public String getPlayerTime(Player player, Koth koth) {
        Integer seconds = koth.getPlayersControlTime().getOrDefault(player, 0);
        if (seconds == 0) {
            return "00:00";
        }
        Integer minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    //#region getKothByLocation
    public boolean isInsideKoth(Koth koth, Location playerLocation) {

        switch (koth.getKothRadius()) {
            case CIRCLE:
                Location loc = koth.getCircleRadius();
                if (loc == null) {
                    return false;
                }
                if (loc.getWorld() != playerLocation.getWorld()) {
                    return false;
                }
                return loc.distance(playerLocation) <= koth.getRadius();
                
            case SQUARE:
                Location loc1 = koth.getBlock1();
                Location loc2 = koth.getBlock2();

                if (loc1 == null || loc2 == null) {
                    return false;
                }

                double x1 = Math.min(loc1.getX(), loc2.getX());
                double y1 = Math.min(loc1.getY(), loc2.getY());
                double z1 = Math.min(loc1.getZ(), loc2.getZ());
        
                double x2 = Math.max(loc1.getX(), loc2.getX());
                double y2 = Math.max(loc1.getY(), loc2.getY());
                double z2 = Math.max(loc1.getZ(), loc2.getZ());
        
                double px = playerLocation.getX();
                double py = playerLocation.getY();
                double pz = playerLocation.getZ();
        
                return px >= x1 && px <= x2 && py >= y1 && py <= y2 && pz >= z1 && pz <= z2;
            default:
                return false;
        }
    }
    //#region addPlayer
    public void addPlayerToKoth(Player player, Koth koth) {
        switch (koth.getMode()) {
            case BY_TIME:
                //Controlled by the KothTask
                break;
            case CONTROL:
                Player king = koth.getKing();
                if (king == null) {
                    koth.setKing(player);
                    return;
                } else if (king == player) {
                    return;
                }
                if (isInsideKoth(koth, king.getLocation())){
                    return;
                } else {
                    koth.setKing(player);
                }
                break;
            case CAPTURE:
                //Controlled by the KothTask
                break;
            default:
                break;
        }
        
    }
    //#region getPlayersIn
    public List<Player> getPlayersInKoth(Koth koth) {
        List<Player> players = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (isInsideKoth(koth, player.getLocation())) {
                players.add(player);
            }
        }
        return players;
    }
    //#region isAvailable
    public boolean isAvailable(Koth koth) {
        switch (koth.getKothRadius()) {
            case CIRCLE:
                if (koth.getCircleRadius() == null || koth.getRadius() == null) {
                    return false;
                }
                break;
            case SQUARE:
                if (koth.getBlock1() == null || koth.getBlock2() == null) {
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }
}
