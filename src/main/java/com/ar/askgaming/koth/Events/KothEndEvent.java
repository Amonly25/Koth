package com.ar.askgaming.koth.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.koth.Koth;

public class KothEndEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    private Koth koth;
    public Koth getKoth() {
        return koth;
    }
    public KothEndEvent(Koth koth) {
        this.koth = koth;
    }
    
}
