import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static javax.swing.UIManager.getInt;

public class Chatter extends JavaPlugin {
    private boolean isLuckPermsAvailable = false;

    @Override
    public void onEnable() {
        // Initialize LuckPerms
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            isLuckPermsAvailable = true;
        }

        String currentVersion = getConfig().getString("version");
        new UpdateChecker(this, 118706, currentVersion).runTaskTimer(this, 0L, 12 * 60 * 60 * 20L);

        // Create ChatCommandExecutor
        ChatCommandExecutor chatCommandExecutor = new ChatCommandExecutor(this);

        // Register commands and their executors
        if (getCommand("reload") != null) {
            getCommand("reload").setExecutor(new Reload(this));
        } else {
            getLogger().warning("Reload command not registered.");
        }

        if (getCommand("help") != null) {
            getCommand("help").setExecutor(new Help());
        } else {
            getLogger().warning("Help command not registered.");
        }

        if (getCommand("distance") != null) {
            getCommand("distance").setExecutor(new Distance(this));
        } else {
            getLogger().warning("Distance command not registered.");
        }

        if (getCommand("globalFormat") != null) {
            getCommand("globalFormat").setExecutor(new globalFormat(this));
        } else {
            getLogger().warning("GlobalFormat command not registered.");
        }

        if (getCommand("localFormat") != null) {
            getCommand("localFormat").setExecutor(new localFormat(this));
        } else {
            getLogger().warning("LocalFormat command not registered.");
        }

        if (getCommand("m") != null) {
            getCommand("m").setExecutor(chatCommandExecutor);
        } else {
            getLogger().warning("M command not registered.");
        }

        // Ensure the default config exists
        saveDefaultConfig();

        // Register event listener
        getServer().getPluginManager().registerEvents(new ChatListener(chatCommandExecutor, this, isLuckPermsAvailable), this);
    }

    public boolean isLuckPermsAvailable() {
        return isLuckPermsAvailable;
    }

    // Method to get the config format
    public String getConfigFormat(String type) {
        return getConfig().getString("chat." + type + ".format", "<%player%> %message%");
    }

    // Method to get the local chat distance
    public int getLocalChatDistance() {
        return getConfig().getInt("chat.local.distance", 50);
    }

    // Method to set and save local chat distance
    public void setLocalChatDistance(int distance) {
        getConfig().set("chat.local.distance", distance);
        saveConfig();
    }

    // Method to set and save global chat format
    public void setGlobalChatFormat(String format) {
        getConfig().set("chat.global.format", format);
        saveConfig();
    }

    // Method to set and save local chat format
    public void setLocalChatFormat(String format) {
        getConfig().set("chat.local.format", format);
        saveConfig();
    }
}
