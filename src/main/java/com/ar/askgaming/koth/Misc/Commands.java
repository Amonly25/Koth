package com.ar.askgaming.koth.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.koth.Koth;
import com.ar.askgaming.koth.KothPlugin;
import com.ar.askgaming.koth.Controllers.KothManager;
import com.ar.askgaming.koth.Controllers.KothManager.KothMode;
import com.ar.askgaming.koth.Controllers.KothManager.KothRadius;
import com.ar.askgaming.koth.Controllers.KothManager.KothState;
import com.ar.askgaming.koth.Controllers.KothManager.KothType;

public class Commands implements TabExecutor{

	private KothPlugin plugin;
    private KothManager manager;

	public Commands(KothPlugin main) {
        plugin = main;
        manager = plugin.getManager();

        plugin.getServer().getPluginCommand("koth").setExecutor(this);
	}
    private String getLang(String key, Player player) {
        return plugin.getLangManager().getFrom(key, player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("Use /command <subcommand>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start":
                start(player, args);             
                break;
            case "stop":
                stop(player, args);
                break;
            case "set":
                set(player, args);
                break;
            case "create":
                create(player, args);
                break;
            case "delete":
                delete(player, args);
                break;
            case "list":
                list(player, args);
                break;
            case "info":
                info(player, args);
                break;
            default:
                sender.sendMessage("Command not found");
            break;
        }
        return true;
    }
    //#region start
    private void start(Player p, String[] args){
        if (!p.hasPermission("koth.admin")) {
            p.sendMessage("§cYou do not have permission to use this command.");
            return;
        }
        if (args.length < 2) {
            p.sendMessage("§cUsage: /koth start <koth>");
            return;
        }
        String kothName = args[1];
        Koth koth = manager.getByName(kothName);
        if (koth == null) {
            p.sendMessage("§cKoth with name " + kothName + " not found.");
            return;
        }
        if (manager.isAvailable(koth)) {
            p.sendMessage("§aKoth " + kothName + " started.");
            manager.startKoth(koth);
        } else {
            p.sendMessage("§cKoth " + kothName + " is not ready to start.");
        }

    }
    //#region stop
    private void stop(Player p, String[] args){
        if (!p.hasPermission("koth.admin")) {
            p.sendMessage("§cYou do not have permission to use this command.");
            return;
        }
        if (args.length < 2) {
            p.sendMessage("§cUsage: /koth stop <koth>");
            return;
        }
        String kothName = args[1];
        Koth koth = manager.getByName(kothName);
        if (koth == null) {
            p.sendMessage("§cKoth with name " + kothName + " not found.");
            return;
        }
        if (koth.getState() == KothState.INPROGRESS) {
            p.sendMessage("§aKoth " + kothName + " stopped.");
            manager.stopKoth(koth);
        } else {
            p.sendMessage("§cKoth " + kothName + " is not running.");
        }
    }
    //#region create
    private void create(Player p, String[] args){
        if (!p.hasPermission("koth.admin")) {
            p.sendMessage("§cYou do not have permission to use this command.");
            return;
        }
        if (args.length < 6) {
            p.sendMessage("§cUsage: /koth create <name> <SOLO/CLAN> <BY_TIME/CONTROL/CAPTURE> <CIRCLE/QUARE> <duration>");
            return;
        }
        String kothName = args[1];
        if (manager.getByName(kothName) != null) {
            p.sendMessage("§cKoth with name " + kothName + " already exists.");
            return;
        }
        KothType type;
        KothMode mode;
        KothRadius radius;
        Integer duration;
        try {
            type = KothType.valueOf(args[2]);
            mode = KothMode.valueOf(args[3]);
            radius = KothRadius.valueOf(args[4]);
            duration = Integer.parseInt(args[5]);
        } catch (Exception e) {
            p.sendMessage("§cInvalid arguments, see console for more info.");
            plugin.getLogger().warning(e.getMessage());
            return;
        }
        manager.createKoth(kothName, type, mode, radius, duration);
        p.sendMessage("§aKoth " + kothName + " created, with type " + type + ", mode " + mode + ", radius " + radius + " and duration " + duration + ".");
        switch (radius) {
            case CIRCLE:
                p.sendMessage("§aUse /koth set " + kothName + " radius <radius> where you want the circle to be.");                
                break;
            default:
                p.sendMessage("§aUse /koth set " + kothName + " block1/block2 and then click a block where you want the square to be.");
                break;
        }
    }
    //#region delete
    private void delete(Player p, String[] args){
        if (!p.hasPermission("koth.admin")) {
            p.sendMessage("§cYou do not have permission to use this command.");
            return;
        }
        if (args.length < 2) {
            p.sendMessage("§cUsage: /koth delete <koth>");
            return;
        }
        String kothName = args[1];
        Koth koth = manager.getByName(kothName);
        if (koth == null) {
            p.sendMessage("§cKoth with name " + kothName + " not found.");
            return;
        }
        p.sendMessage("§aKoth " + kothName + " deleted.");
        if (koth.getState() == KothState.INPROGRESS) {
            manager.stopKoth(koth);
        }
        manager.deleteKoth(koth);

    }
    //#region set
    private void set(Player p, String[] args){
        if (!p.hasPermission("koth.admin")) {
            p.sendMessage("§cYou do not have permission to use this command.");
            return;
        }

        if (args.length < 2) {
            p.sendMessage("§cUsage: /koth set koth <radius/block1/block2/show_borders/minimum_players>");
            return;
        }
        String kothName = args[1];
        Koth koth = manager.getByName(kothName);
        if (koth == null) {
            p.sendMessage("§cKoth with name " + kothName + " not found.");
            return;
        }
        if (args.length < 3) {
            p.sendMessage("§cUsage: /koth set " + kothName + " <radius/block1/block2/show_borders/minimum_players>");
            return;
        }
        switch (args[2].toLowerCase()) {
            case "radius":
                if (args.length < 4){
                    p.sendMessage("§cUsage: /koth set " + kothName + " radius <radius>");
                    return;
                }
                try {
                    int radius = Integer.parseInt(args[3]);
                    koth.setRadius(radius);
                    koth.setCircleRadius(p.getLocation());
                    p.sendMessage("§aRadius set for koth " + kothName + ".");
                    manager.saveKoths();
                } catch (Exception e) {
                    p.sendMessage("§cInvalid radius, enter a valid number.");
                }
                break;
            case "block1":
                if (koth.getKothRadius() != KothRadius.SQUARE) {
                    p.sendMessage("§cKoth " + kothName + " is not in square mode.");
                    return;
                }
                manager.getEditingKoths().put(p, koth);
                p.sendMessage("§aLeft click a block where you want the square to start.");
                break;
            case "block2":
                if (koth.getKothRadius() != KothRadius.SQUARE) {
                    p.sendMessage("§cKoth " + kothName + " is not in square mode.");
                    return;
                }
                manager.getEditingKoths().put(p, koth);
                p.sendMessage("§aRight click a block where you want the square to end.");
                break;
            case "show_borders":
                if (args.length < 4){
                    p.sendMessage("§cUsage: /koth set " + kothName + " show_borders <true/false>");
                    return;
                }
                try {
                    boolean show = Boolean.parseBoolean(args[3]);
                    koth.setShowBorders(show);
                    p.sendMessage("§aShow borders set for koth " + kothName + ".");
                    manager.saveKoths();
                } catch (Exception e) {
                    p.sendMessage("§cInvalid argument, use true or false.");
                }
                break;
            case "minimum_players":
                if (args.length < 4){
                    p.sendMessage("§cUsage: /koth set " + kothName + " minimum_players <number>");
                    return;
                }
                try {
                    int min = Integer.parseInt(args[3]);
                    koth.setMinimunPlayers(min);
                    p.sendMessage("§aMinimum players set for koth " + kothName + ".");
                    manager.saveKoths();
                } catch (Exception e) {
                    p.sendMessage("§cInvalid argument, enter a valid number.");
                }
                break;
            default:
                p.sendMessage("§cInvalid argument, use radius/block1/block2/show_borders/minimum_players.");
                break;
        }

    }
    //#region list
    private void list(Player p, String[] args){

        p.sendMessage("§aKoths:");
        for (Koth koth : manager.getKoths()) {
            p.sendMessage("§a- " + koth.getName() + " (" + koth.getType().name() + ")");
        }

    }
    //#region info
    private void info(Player p, String[] args){
        if (args.length < 2) {
            p.sendMessage("§cUsage: /koth info <koth>");
            return;
        }
        String kothName = args[1];
        Koth koth = manager.getByName(kothName);
        if (koth == null) {
            p.sendMessage(getLang("not_found", p));
            return;
        }
        p.sendMessage("§aKoth " + koth.getName() + ":");
        p.sendMessage(getLang("info.type", p) + koth.getType().name());
        p.sendMessage(getLang("info.mode", p) + koth.getMode().name());
        p.sendMessage(getLang("info.radius", p) + koth.getKothRadius().name());
        p.sendMessage(getLang("info.duration", p) + koth.getDuration());
        p.sendMessage(getLang("info.state", p) + koth.getState().name());
        p.sendMessage(getLang("info.borders", p) + koth.isShowBorders());
        if (koth.getKothRadius() == KothRadius.CIRCLE) {
            p.sendMessage(getLang("info.location", p) + koth.getCircleRadius().getBlockX() + ", " + koth.getCircleRadius().getBlockY() + ", " + koth.getCircleRadius().getBlockZ());
        } else {
            p.sendMessage(getLang("info.location", p) + koth.getBlock1().getBlockX() + ", " + koth.getBlock1().getBlockY() + ", " + koth.getBlock1().getBlockZ());
            p.sendMessage(getLang("info.location", p) + koth.getBlock2().getBlockX() + ", " + koth.getBlock2().getBlockY() + ", " + koth.getBlock2().getBlockZ());
        }

    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            List<String> commands = new ArrayList<>(Arrays.asList("info","list"));
            if (sender.hasPermission("koth.admin")) {
                commands.add("create");
                commands.add("delete");
                commands.add("start");
                commands.add("stop");
                commands.add("set");
            }
            return commands;
        }
        if (args.length == 2) {
            List<String> koths = new ArrayList<>();
            for (Koth koth : manager.getKoths()) {
                koths.add(koth.getName());
            }
            return koths;
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                List<String> set = new ArrayList<>(Arrays.asList("radius","block1","block2","show_borders","minimum_players"));
                return set;
            }
            if (args[0].equalsIgnoreCase("create")) {
                List<String> types = new ArrayList<>(Arrays.asList("SOLO","CLAN"));
                return types;
            }
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            List<String> modes = new ArrayList<>(Arrays.asList("BY_TIME","CONTROL","CAPTURE"));
            return modes;
        }
        if (args.length == 5 && args[0].equalsIgnoreCase("create")) {
            List<String> radius = new ArrayList<>(Arrays.asList("CIRCLE","SQUARE"));
            return radius;
        }

        return null;
    }
}