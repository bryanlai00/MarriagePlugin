package me.gnarzy.marryplugin.commands;

import me.gnarzy.marryplugin.MarryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarryCommand implements CommandExecutor {
    MarryPlugin plugin = MarryPlugin.plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If the command executor is a player:
        if (sender instanceof Player) {
            //Set a variable player representing the command executor.
            Player player = (Player) sender;
            if(args.length == 2) {
                //See if the player is online.
                Player partner = Bukkit.getPlayerExact(args[1]);
                String action = args[0];
                if(partner != null) {
                    if(partner.getName() != player.getName()) {
                        player.sendMessage("You can't marry yourself dude.");
                    }
                    else {
                        if (action.equals("propose")) {

                        }
                        if (action.equals("accept")) {

                        }
                        if (action.equals("deny")) {

                        }
                    }
                }
                //Cases when the player is not online
                else {
                    //Divorce can occur when the other player is NOT online.
                    if (action.equals("divorce")) {
                        if(plugin.getConfig().getString(player.getName() + ".partner").equals(partner.getName())) {
                            plugin.getConfig().set(player.getName() + ".partner", "Single");
                            plugin.getConfig().set(partner.getName() + ".partner", "Single");
                            Bukkit.broadcastMessage(player.getName() + " has divorced " + partner.getName());
                        }
                    }
                    else {
                        player.sendMessage(partner.getName() + " is currently not online.");
                    }
                }
            }
        }


        return false;
    }
}
