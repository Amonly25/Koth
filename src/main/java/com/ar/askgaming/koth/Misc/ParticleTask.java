package com.ar.askgaming.koth.Misc;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;
import com.ar.askgaming.koth.Controllers.KothManager;

public class ParticleTask extends BukkitRunnable {
    
    private KothPlugin plugin;
    private KothManager manager;
    public ParticleTask(KothPlugin main, KothManager manager) {
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
            if (!koth.isShowBorders()) {continue;}
            switch (koth.getKothRadius()) {
                case CIRCLE:
                    if (koth.getLoc() != null){
                        generateParticlesInAirBlocks(koth.getLoc(), koth.getRadius()); 
                    }
                    
                    break;
                case SQUARE:
                    Location loc1 = koth.getBlock1();
                    Location loc2 = koth.getBlock2();

                    if (loc1 != null && loc2 != null){
                        generateParticles(loc1,loc2);  
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);

    private void generateParticlesInAirBlocks(Location center, int radius) {

        World world = center.getWorld();
        if (world == null) {return;}

        int squaredRadius = radius * radius;
        int squaredRadiusMinusOne = (radius - 1) * (radius - 1);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int distanceSquared = x * x + y * y + z * z;
                    if (distanceSquared <= squaredRadius && distanceSquared > squaredRadiusMinusOne) {
                        Location loc = center.clone().add(x, y, z);
                        if (world.getBlockAt(loc).getType() == Material.AIR) {
                            world.spawnParticle(Particle.DUST, loc.add(0.5, 0.5, 0.5), 1, dustOptions);
                        }
                    }
                }
            }
        }
    }
    private void generateParticles(Location loc1, Location loc2) {
        double x1 = Math.min(loc1.getX(), loc2.getX());
        double y1 = Math.min(loc1.getY(), loc2.getY());
        double z1 = Math.min(loc1.getZ(), loc2.getZ());

        double x2 = Math.max(loc1.getX(), loc2.getX());
        double y2 = Math.max(loc1.getY(), loc2.getY());
        double z2 = Math.max(loc1.getZ(), loc2.getZ());

        // Generate particles along the X edges
        for (double x = x1; x <= x2; x++) {
            loc1.getWorld().spawnParticle(Particle.DUST, x, y1, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x, y1, z2, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x, y2, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x, y2, z2, 1, 0, 0, 0, 0, dustOptions);
        }

        // Generate particles along the Y edges
        for (double y = y1; y <= y2; y++) {
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y, z2, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y, z1, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y, z2, 1, 0, 0, 0, 0, dustOptions);
        }

        // Generate particles along the Z edges
        for (double z = z1; z <= z2; z++) {
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y1, z, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x1, y2, z, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y1, z, 1, 0, 0, 0, 0, dustOptions);
            loc1.getWorld().spawnParticle(Particle.DUST, x2, y2, z, 1, 0, 0, 0, 0, dustOptions);
        }
    }
}
