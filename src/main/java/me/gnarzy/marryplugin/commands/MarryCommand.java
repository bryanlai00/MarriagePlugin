package me.gnarzy.marryplugin.commands;

import me.gnarzy.marryplugin.MarryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MarryCommand implements CommandExecutor {
    ArrayList<String[]> proposals = new ArrayList<String[]>();
    public static MarryCommand marryCommand;
    //Getting an instance of the MarryPlugin. (Important for updating the config file)
    //This is used to determine whether a player is Single or Married to another.
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
                        player.sendMessage("You can't marry yourself...");
                    }
                    else {
                        if (action.equals("propose")) {
                            if(plugin.getConfig().getString(player.getName() + ".partner").equals("Single") && plugin.getConfig().getString(partner.getName() + ".partner").equals("Single")){
                                proposals.add(new String[]{player.getName(), partner.getName()});
                                player.sendMessage("You have proposed to " + partner.getName() + "!");
                                partner.sendMessage(player.getName() + " has proposed to you! It is up to you to accept or decline.");
                            }
                            else {
                                //Disable polygamy. You cannot marry another while already married.
                                if(!plugin.getConfig().getString(player.getName() + ".partner").equals("Single")) {
                                    player.sendMessage("You are married to another!");
                                }
                                if(!plugin.getConfig().getString(partner.getName() + ".partner").equals("Single")) {
                                    player.sendMessage("That person is married to " + plugin.getConfig().getString(partner.getName() + ".partner"));
                                }
                            }
                        }
                        if (action.equals("accept")) {
                            for(int i = 0; i < proposals.size(); i++) {
                                String[] proposal = proposals.get(i);
                                //The first value of the pair is the partner's name. (THE OTHER PLAYER)
                                //This may seem confusing but consider the fact that /marry propose is run by the player
                                //while /marry accept is being run by the partner. The partner of the partner is the player.
                                if(proposal[0].equals(partner.getName()) && proposal[1].equals(player.getName())) {
                                    plugin.getConfig().set(player.getName() + ".partner", partner.getName());
                                    plugin.getConfig().set(partner.getName() + ".partner", player.getName());
                                    plugin.saveConfig();
                                    plugin.reloadConfig();
                                    proposals.remove(i);
                                    Bukkit.broadcastMessage(player.getName() + " and " + partner.getName() + " are now married!");
                                    break;
                                }
                            }
                        }
                        if (action.equals("decline")) {

                        }
                    }
                }
                //Cases when the player is not online
                else {
                    //Divorce can occur when the other player is NOT online.
                    if (action.equals("divorce")) {
                        if(plugin.getConfig().getString(player.getName() + ".partner").equals(args[1])) {
                            plugin.getConfig().set(player.getName() + ".partner", "Single");
                            plugin.getConfig().set(args[1] + ".partner", "Single");
                            Bukkit.broadcastMessage(player.getName() + " has divorced " + args[1]);
                        }
                    }
                    else {
                        player.sendMessage(args[1] + " is currently not online.");
                    }
                }
            }
        }


        return false;
    }
}
