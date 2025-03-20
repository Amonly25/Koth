package com.ar.askgaming.koth.Controllers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;
import com.ar.askgaming.koth.Misc.ParticleTask;

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

    private List<Koth> koths;
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
        WAITING_NEXT, INPROGRESS, FINISHED, EDIT_MODE
    }
    public enum KothType {
        SOLO, CLAN
    }
    public enum KothMode {
        TIME_BASED, 
        CONTROL_POINT,
        PROGRESSIVE_CAP
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
        koth.setState(KothState.INPROGRESS);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(getLang("start", player).replace("{koth}", koth.getName()));
        }
    }
    //#region delete
    public void deleteKoth(Koth koth) {
        koths.remove(koth);
        saveKoths();
    }
    //#region end
    public void endKoth(Koth koth) {
    }
    //#region stop
    public void stopKoth(Koth Koth) {
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
    //#region getKothByLocation
    public boolean isInsideKoth(Koth koth, Location playerLocation) {

        switch (koth.getKothRadius()) {
            case CIRCLE:
                Location loc = koth.getLoc();
                return loc.distance(playerLocation) <= koth.getRadius();
                

            case SQUARE:
                Location loc1 = koth.getBlock1();
                Location loc2 = koth.getBlock2();

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
        
    }
    //#region isAvailable
    public boolean isAvailable(Koth koth) {
        switch (koth.getKothRadius()) {
            case CIRCLE:
                if (koth.getLoc() == null) {
                    return false;
                }
            case SQUARE:
                if (koth.getBlock1() == null || koth.getBlock2() == null) {
                    return false;
                }
            default:
                break;
        }
        if (koth.getState() == KothState.WAITING_NEXT) {
            return true;
        }
        return false;
    }
}
