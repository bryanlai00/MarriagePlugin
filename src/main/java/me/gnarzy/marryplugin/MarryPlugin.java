package me.gnarzy.marryplugin;

import me.gnarzy.marryplugin.commands.MarryCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class MarryPlugin extends JavaPlugin implements Listener {
    public static MarryPlugin plugin;
    //Getting an instance of the MarryCommand.
    MarryCommand marryCommand = MarryCommand.marryCommand;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.saveConfig();
        this.saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        getLogger().info("Marriage Plugin enabling...");
        getCommand("marry").setExecutor(new MarryCommand());
    }

    public void onDisable() {
        plugin = null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(getConfig().contains(e.getPlayer().getName())){
            getLogger().warning("Player " + e.getPlayer().getName() + " exists in config.");
        } else {
            getConfig().set(e.getPlayer().getName() + ".partner", "Single");
            this.saveConfig();
            this.reloadConfig();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        //Removing proposals if either player quits the server.
        ArrayList<String[]> proposals = marryCommand.proposals;
        for(int i = 0; i < proposals.size(); i++) {
            String[] proposal = proposals.get(i);
            if(proposal[0].equals(e.getPlayer().getName()) || proposal[1].equals(e.getPlayer().getName())) {
                proposals.remove(i);
            }
        }
    }
}
