import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Chatter extends JavaPlugin {
    private boolean isLuckPermsAvailable = false;

    @Override
    public void onEnable() {
        // Initialize LuckPerms
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            isLuckPermsAvailable = true;
        }

        // Ensure the default config exists
        saveDefaultConfig();

        // Create ChatCommandExecutor
        ChatCommandExecutor chatCommandExecutor = new ChatCommandExecutor(this);

        // Register commands and their executors
        registerCommands();

        // Register event listener
        getServer().getPluginManager().registerEvents(new ChatListener(chatCommandExecutor, this, isLuckPermsAvailable), this);
    }

    private void registerCommands() {
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

        if (getCommand("m") != null) {
            getCommand("m").setExecutor(new DM());
        } else {
            getLogger().warning("DM command not registered.");
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
    }

    public boolean isLuckPermsAvailable() {
        return isLuckPermsAvailable;
    }

    // Method to get the config format
    public String getConfigFormat(String type) {
        return getConfig().getString("chat." + type + ".format", "<%player%> %message%");
    }


    // Method to set and save global chat format
    public boolean isBlacklistEnabled() {
        return getConfig().getBoolean("Blacklist.Enabled", false);
    }

    public List<String> getBlacklistWords() {
        return getConfig().getStringList("Blacklist.List");
    }

    public String getBlacklistMessage() {
        return getConfig().getString("Blacklist.Message", "&cThis word is blacklisted.");
    }

    public boolean isReplaceEnabled() {
        return getConfig().getBoolean("Blacklist.Replace.Enabled", false);
    }

    public String getReplacementMessage() {
        return getConfig().getString("Blacklist.Replace.message", "❤❤❤");
    }

    public String getGlobalChatFormat() {
        return getConfig().getString("chat.global.format", "&7[&bGlobal&7] &a%player%: &f%message%");
    }

    public String getLocalChatFormat() {
        return getConfig().getString("chat.local.format", "&7[&bLocal&7] &a%player%: &f%message%");
    }

    public int getLocalChatDistance() {
        return getConfig().getInt("chat.local.distance", 100);
    }
}
