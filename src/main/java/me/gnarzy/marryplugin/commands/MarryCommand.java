package me.gnarzy.marryplugin.commands;

import me.gnarzy.marryplugin.MarryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MarryCommand implements CommandExecutor {
    public ArrayList<String[]> proposals = new ArrayList<String[]>();
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
            if(args.length == 1) {

                //Help messages.
                if(args[0].equals("help")) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Marry Commands");
                    player.sendMessage(ChatColor.GOLD + "/marry help:" + ChatColor.WHITE + " Lists options for the marry command.");
                    player.sendMessage(ChatColor.GOLD + "/marry divorce:" + ChatColor.WHITE + " Divorce your partner.");
                    player.sendMessage(ChatColor.GOLD + "/marry info:" + ChatColor.WHITE + " Tells you information about your marriage.");
                    player.sendMessage(ChatColor.GOLD + "/marry tp:" + ChatColor.WHITE + " Teleport to your partner if they are online.");
                    player.sendMessage(ChatColor.GOLD + "/marry propose [player]:" + ChatColor.WHITE + " Propose to a specific player.");
                    player.sendMessage(ChatColor.GOLD + "/marry accept [player]:" + ChatColor.WHITE + " Accept a marriage proposal.");
                    player.sendMessage(ChatColor.GOLD + "/marry decline [player]:" + ChatColor.WHITE + " Decline a marriage proposal.");
                }

                //Divorcing.
                if (args[0].equals("divorce")) {
                    String partner = plugin.getConfig().getString(player.getName() + ".partner");
                    if(!plugin.getConfig().getString(player.getName() + ".partner").equals("Single")) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " has divorced " + ChatColor.GREEN + partner + ChatColor.WHITE + "!");
                        plugin.getConfig().set(player.getName() + ".partner", "Single");
                        plugin.getConfig().set(partner + ".partner", "Single");
                        plugin.saveConfig();
                        plugin.reloadConfig();
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "You aren't married to anyone.");
                    }
                }

                //Info about marriage.
                if(args[0].equals("info")) {
                    String partner = plugin.getConfig().getString(player.getName() + ".partner");
                    if(!partner.equals("Single")) {
                        player.sendMessage("You are married to " + ChatColor.GREEN + partner);
                    }
                    else {
                        player.sendMessage("You are currently " + ChatColor.RED + "single.");
                    }
                }

                //Teleport to partner if they are online.
                if(args[0].equals("tp")) {
                    String partnerName = plugin.getConfig().getString(player.getName() + ".partner");
                    if(partnerName.equals("Single")) {
                        player.sendMessage("You cannot tp. You are currently " + ChatColor.RED + "single.");
                    }
                    else {
                        Player partner = Bukkit.getPlayerExact(partnerName);
                        try {
                            player.teleport(partner.getLocation());
                            partner.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " has teleported to you.");
                        }
                        catch(NullPointerException e) {
                            player.sendMessage("Your partner is not " + ChatColor.RED + "online!");
                        }
                    }
                }
            }
            if(args.length == 2) {
                //See if the player is online.
                Player partner = Bukkit.getPlayerExact(args[1]);
                String action = args[0];
                if(partner != null) {
                    if(partner.getName() == player.getName()) {
                        player.sendMessage("You can't marry yourself...");
                    }
                    else {
                        if (action.equals("propose")) {
                            if(plugin.getConfig().getString(player.getName() + ".partner").equals("Single") && plugin.getConfig().getString(partner.getName() + ".partner").equals("Single")){
                                proposals.add(new String[]{player.getName(), partner.getName()});
                                player.sendMessage("You have proposed to " + ChatColor.GREEN + partner.getName() + ChatColor.WHITE + "!");
                                partner.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " has proposed to you! It is up to you to accept or decline.");
                            }
                            else {
                                //Disable polygamy. You cannot marry another while already married.
                                if(!plugin.getConfig().getString(player.getName() + ".partner").equals("Single")) {
                                    player.sendMessage(ChatColor.RED + "You are married to another!");
                                }
                                if(!plugin.getConfig().getString(partner.getName() + ".partner").equals("Single")) {
                                    player.sendMessage(ChatColor.RED + "That person is married to " + plugin.getConfig().getString(partner.getName() + ".partner"));
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
                                    Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " and " + ChatColor.GREEN + partner.getName() + ChatColor.WHITE + " are now married!");
                                    break;
                                }
                            }
                        }
                        if (action.equals("decline")) {
                            for(int i = 0; i < proposals.size(); i++) {
                                String[] proposal = proposals.get(i);
                                //The first value of the pair is the partner's name. (THE OTHER PLAYER)
                                //This may seem confusing but consider the fact that /marry propose is run by the player
                                //while /marry accept is being run by the partner. The partner of the partner is the player.
                                if (proposal[0].equals(partner.getName()) && proposal[1].equals(player.getName())) {
                                    proposals.remove(i);
                                    break;
                                }
                            }
                            player.sendMessage(ChatColor.RED + "You have rejected " + partner.getName() + "'s proposal.");
                            partner.sendMessage(ChatColor.RED + "Your marriage proposal was rejected by " + player.getName());
                        }
                    }
                }
                //Cases when the player is not online
                else {
                        player.sendMessage(ChatColor.RED + args[1] + ChatColor.WHITE +  " is currently not online.");
                }
            }
        }


        return false;
    }
}
