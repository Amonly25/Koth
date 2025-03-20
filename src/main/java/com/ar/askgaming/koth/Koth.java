package com.ar.askgaming.koth;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.ar.askgaming.koth.Controllers.KothManager.KothMode;
import com.ar.askgaming.koth.Controllers.KothManager.KothRadius;
import com.ar.askgaming.koth.Controllers.KothManager.KothState;
import com.ar.askgaming.koth.Controllers.KothManager.KothType;

public class Koth implements ConfigurationSerializable{
    
    private KothPlugin plugin = KothPlugin.getInstance();

    public Koth(String name, KothType type, KothMode mode, KothRadius radius, Integer time) {
        this.name = name;
        this.type = type;
        this.mode = mode;
        this.kothRadius = radius;
        this.duration = time;
        this.countdown = time;
        this.minimunPlayers = 1;

        createBossBar();
    }

    public Koth(Map<String, Object> map) {
        this.block1 = (Location) map.get("block1");
        this.block2 = (Location) map.get("block2");
        this.circleRadius = (Location) map.get("circleRadius");
        this.radius = (Integer) map.get("radius");
        this.name = (String) map.get("name");
        this.showBorders = (boolean) map.get("showBorders");
        this.kothRadius = KothRadius.valueOf((String) map.get("kothRadius"));
        this.type = KothType.valueOf((String) map.get("type"));
        this.mode = KothMode.valueOf((String) map.get("mode"));
        this.duration = (Integer) map.get("duration");
        this.countdown = duration;
        this.minimunPlayers = (Integer) map.get("minimunPlayers");

        createBossBar();
    }

    @Override
    public Map<String, Object> serialize() {
        
        Map<String, Object> map = new HashMap<>();
        map.put("block1", block1);
        map.put("block2", block2);
        map.put("circleRadius", circleRadius);
        map.put("radius", radius);
        map.put("name", name);
        map.put("showBorders", showBorders);
        map.put("kothRadius", kothRadius.name());
        map.put("type", type.name());
        map.put("mode", mode.name());
        map.put("duration", duration);
        map.put("minimunPlayers", minimunPlayers);
        return map;
        
    }
    public void reset() {
        this.king = null;
        this.lastKing = null;
        this.countdown = duration;
        this.state = KothState.WAITING_NEXT;
        this.playersControlTime.clear();
        if (mode == KothMode.CAPTURE) {
            bossBar.removeAll();
        }
    }

    private BossBar bossBar;
    public BossBar getBossBar() {
        return bossBar;
    }

    public void createBossBar() {
        if (mode != KothMode.CAPTURE) {
            return;
        }
        bossBar = plugin.getServer().createBossBar("Koth", BarColor.RED, BarStyle.SOLID);
        
    }
    public void updateBossBar() {
        if (bossBar == null) {
            return;
        }
        if (king == null) {
            bossBar.setTitle(plugin.getConfig().getString("bossbar.no_king"));
        } else {
            bossBar.setTitle(plugin.getConfig().getString("bossbar.king").replace("%player%", king.getName()));
        }
        bossBar.setProgress(1.0 - ((double) countdown / duration));

    }

    private Location block1, block2, circleRadius;
    private String name;
    private boolean showBorders = true;
    private KothRadius kothRadius;
    private KothState state = KothState.WAITING_NEXT;
    private KothType type;
    private KothMode mode;
    private Player king, lastKing = null;
    private Integer countdown, duration, radius, minimunPlayers;
    private HashMap<Player, Integer> playersControlTime = new HashMap<>();

    public Integer getMinimunPlayers() {
        return minimunPlayers;
    }

    public void setMinimunPlayers(Integer minimunPlayers) {
        this.minimunPlayers = minimunPlayers;
    }

    public void setCountdown(Integer countdown) {
        this.countdown = countdown;
    }
    public Location getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(Location circleRadius) {
        this.circleRadius = circleRadius;
    }
    public Integer getCountdown() {
        return countdown;
    }
    public boolean isShowBorders() {
        return showBorders;
    }
    public void setShowBorders(boolean showBorders) {
        this.showBorders = showBorders;
    }
    public HashMap<Player, Integer> getPlayersControlTime() {
        return playersControlTime;
    }
    public Location getBlock1() {
        return block1;
    }
    public void setBlock1(Location block1) {
        this.block1 = block1;
    }
    public Integer getDuration() {
        return duration;
    }
    public Location getBlock2() {
        return block2;
    }
    public void setBlock2(Location block2) {
        this.block2 = block2;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public KothRadius getKothRadius() {
        return kothRadius;
    }
    public void setKothRadius(KothRadius kothRadius) {
        this.kothRadius = kothRadius;
    }
    public KothState getState() {
        return state;
    }
    public void setState(KothState state) {
        this.state = state;
    }
    public KothType getType() {
        return type;
    }
    public void setType(KothType type) {
        this.type = type;
    }
    public KothMode getMode() {
        return mode;
    }
    public void setMode(KothMode mode) {
        this.mode = mode;
    }
    public Player getKing() {
        return king;
    }
    public void setKing(Player king) {
        this.king = king;
    }
    public Player getLastKing() {
        return lastKing;
    }
    public void setLastKing(Player lastKing) {
        this.lastKing = lastKing;
    }
    public Integer getRadius() {
        return radius;
    }
    public void setRadius(Integer radius) {
        this.radius = radius;
    }
}
