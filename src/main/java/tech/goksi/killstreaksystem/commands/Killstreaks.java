package tech.goksi.killstreaksystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.goksi.killstreaksystem.Main;


public class Killstreaks implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(args.length > 0){
            //killstreaks reload
            if(args[0].equalsIgnoreCase("reload")){
                if(p.hasPermission("killstreaks.reload")){
                    Main.getInstance().reloadConfig();
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.Reload")));
                    return true;
                }else p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.NoPermission")));
                //killstreaks set
            }else if (args[0].equalsIgnoreCase("set")){
                if(p.hasPermission("killstreaks.set")){
                    if(args.length == 3){
                        // ovde set ks
                    }else p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.SetWrongUsage")));
                }else p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.NoPermission")));
                //killstreaks <player>
            }else {
                //case sensitive je
                Player target = Bukkit.getPlayer(Main.getInstance().getDatabase().getUUID(args[0]));
                if(target == null){
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.NotInDatabase").
                            replaceAll("%player", args[0])));
                    return true;
                }else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.Killstreaks")
                            .replaceAll("%player", target.getName())
                            .replaceAll("%currentKs", String.valueOf(Main.getInstance().getDatabase().getKillStreaks(target)))
                            .replaceAll("%biggestKs", String.valueOf(Main.getInstance().getDatabase().getBiggestKS(target)))
                            .replaceAll("%award", "toDo")));
                    return true;
                }


            }
            //bez ikakvih argumenata
        }else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.Killstreaks")
                    .replaceAll("%player", p.getName())
                    .replaceAll("%currentKs", String.valueOf(Main.getInstance().getDatabase().getKillStreaks(p)))
                    .replaceAll("%biggestKs", String.valueOf(Main.getInstance().getDatabase().getBiggestKS(p)))
                    .replaceAll("%award", "toDo")));
            return true;

        }

        return false;
    }
}
