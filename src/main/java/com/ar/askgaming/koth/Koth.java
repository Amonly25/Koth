package com.ar.askgaming.koth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.ar.askgaming.koth.Controllers.KothManager.KothMode;
import com.ar.askgaming.koth.Controllers.KothManager.KothRadius;
import com.ar.askgaming.koth.Controllers.KothManager.KothState;
import com.ar.askgaming.koth.Controllers.KothManager.KothType;

public class Koth implements ConfigurationSerializable{
    
    public Koth(String name, KothType type, KothMode mode, KothRadius radius, Integer time) {
        this.name = name;
        this.type = type;
        this.mode = mode;
        this.kothRadius = radius;
        this.countdown = time;
    }

    public Koth(Map<String, Object> map) {
        this.block1 = (Location) map.get("block1");
        this.block2 = (Location) map.get("block2");
        this.loc = (Location) map.get("loc");
        this.radius = (Integer) map.get("radius");
        this.name = (String) map.get("name");
        this.showBorders = (boolean) map.get("showBorders");
        this.kothRadius = (KothRadius) map.get("kothRadius");
        this.state = (KothState) map.get("state");
        this.type = (KothType) map.get("type");
        this.mode = (KothMode) map.get("mode");
        this.king = (Player) map.get("king");
        this.lastKing = (Player) map.get("lastKing");
        this.countdown = (Integer) map.get("countdown");
        this.playersControlTime = (HashMap<Player, Integer>) map.get("playersControlTime");
        this.commands = (List<String>) map.get("commands");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("block1", block1);
        map.put("block2", block2);
        map.put("loc", loc);
        map.put("radius", radius);
        map.put("name", name);
        map.put("showBorders", showBorders);
        map.put("kothRadius", kothRadius);
        map.put("state", state);
        map.put("type", type);
        map.put("mode", mode);
        map.put("king", king);
        map.put("lastKing", lastKing);
        map.put("countdown", countdown);
        map.put("playersControlTime", playersControlTime);
        map.put("commands", commands);
        return map;
        
    }
    private Location block1;
    private Location block2;
    private Location loc;
    private Integer radius;
    private String name;
    private boolean showBorders = true;
    private KothRadius kothRadius;
    private KothState state;
    private KothType type;
    private KothMode mode;

    private Player king;
    private Player lastKing;

    private Integer countdown;
    private HashMap<Player, Integer> playersControlTime = new HashMap<>();
    private List<String> commands = new ArrayList<>();

    public void setCountdown(Integer countdown) {
        this.countdown = countdown;
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
    public Location getBlock2() {
        return block2;
    }
    public void setBlock2(Location block2) {
        this.block2 = block2;
    }
    public Location getLoc() {
        return loc;
    }
    public void setLoc(Location loc) {
        this.loc = loc;
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
