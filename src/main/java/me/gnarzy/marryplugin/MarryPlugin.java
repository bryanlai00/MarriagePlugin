package me.gnarzy.marryplugin;

import me.gnarzy.marryplugin.commands.MarryCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MarryPlugin extends JavaPlugin implements Listener {
    public static MarryPlugin plugin;

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
    public void onLeave(PlayerBedLeaveEvent e) {

    }
}
