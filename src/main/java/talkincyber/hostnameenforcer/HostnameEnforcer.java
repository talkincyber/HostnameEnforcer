package talkincyber.hostnameenforcer;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public final class HostnameEnforcer extends JavaPlugin implements Listener, CommandExecutor {

    FileConfiguration config = this.getConfig();

    // Method for ping events
    @EventHandler
    public void onStatusCheck(PaperServerListPingEvent ping) {
        // Get enable-logging setting from config file
        boolean enableLogging = config.getBoolean("enable-logging");
        // Get list of configured hostnames from config
        List<String> hostNames = config.getStringList("hostname");

        // getHostName can be null, have to try and catch, honestly not sure why...
        try {
            String connectionHostname = ping.getClient().getVirtualHost().getHostName();

            // This boolean is used to check if the hostname of the connection is acceptable
            boolean isAcceptable = false;

            // Loop through the hostnames
            for (int i = 0; i < hostNames.size(); i++)
                // If the connection hostname matches any in the config, break and allow the ping
                if (connectionHostname.equals(hostNames.get(i))) {
                    isAcceptable = true;
                    break;
                }
            // If the hostname is not acceptable and logging is on, cancel and log to console
            if (!isAcceptable && enableLogging) {
                ping.setCancelled(true);
                getLogger().warning("Ping using hostname: " + connectionHostname + " blocked.");
            }

            // If the hostname is not acceptable and logging is off, cancel quietly
            else if (!isAcceptable && !enableLogging) {
                ping.setCancelled(true);
            }
        } catch (NullPointerException ignored) {
            ping.setCancelled(true);
        }

    }

    // Method for handling player logins
    @EventHandler
    public void onConnection(PlayerLoginEvent login) {
        // Get list of configured hostnames from config
        List<String> hostNames = config.getStringList("hostname");
        // Get port of server from config
        String port = ":" + config.getString("port");
        // The connection hostname sent by client
        String connectionHostName = login.getHostname();
        // Get custom kick message from config
        String kickMessage = config.getString("kick-message");
        // Set boolean to false by default, change if it's acceptable for connection
        boolean isAcceptable = false;

        try {
            // Loop through the hostnames
            for (int i = 0; i < hostNames.size(); i++) {
                if (connectionHostName.equals(hostNames.get(i) + port)) {
                    isAcceptable = true;
                    break;
                }
            }

            // If the client sent hostname is not acceptable, kick the player.
            if (!isAcceptable) {
                login.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text(kickMessage));
            }

        } catch (NullPointerException e) {
            getLogger().warning(e.getMessage());
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        HostnameEnforcer plugin = this;
        plugin.saveDefaultConfig();

        getLogger().info("\u001B[32mChecking if plugin is enabled...\u001B[0m");

        // Check config if plugin is enabled
        boolean pluginEnabled = config.getBoolean("enabled");
        // If not enabled, log and disable
        if (! pluginEnabled ) {
            getLogger().info("\u001B[31mPlugin Disabled...\u001B[0m");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Create data folder if it does not exist
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        // Register the server
        getServer().getPluginManager().registerEvents(plugin, (Plugin) plugin);
        plugin.saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("\u001B[31mWe goin down, out this hoe!\u001B[0m");
    }
}
