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
import com.ar.askgaming.koth.Controllers.KothManager.KothState;

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
        if (args.length == 1) {
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

    }
    //#region create
    private void create(Player p, String[] args){

    }
    //#region delete
    private void delete(Player p, String[] args){

    }
    //#region set
    private void set(Player p, String[] args){

    }
    //#region list
    private void list(Player p, String[] args){

    }
    //#region info
    private void info(Player p, String[] args){

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
        return null;
    }
}