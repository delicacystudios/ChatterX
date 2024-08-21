import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class ChatCommandExecutor implements CommandExecutor {

    private final Chatter plugin;

    public ChatCommandExecutor(Chatter plugin) {
        this.plugin = plugin;
    }

    void setGlobalChat(Player player, boolean isGlobal) {
        if (plugin == null) {
            player.sendMessage(ChatColor.RED + "Plugin instance is null. Please contact the server administrator.");
            return;
        }
        FileConfiguration config = plugin.getConfig();
        config.set("players." + player.getUniqueId() + ".global", isGlobal);
        plugin.saveConfig();
    }

    public boolean isGlobalChat(Player player) {
        if (plugin == null) {
            player.sendMessage(ChatColor.RED + "Plugin instance is null. Please contact the server administrator.");
            return false;
        }
        return plugin.getConfig().getBoolean("players." + player.getUniqueId() + ".global", false);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("m")) {
                if (args.length >= 2) {
                    Player target = plugin.getServer().getPlayer(args[0]);
                    if (target != null && target.isOnline()) {
                        StringBuilder messageBuilder = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            if (i > 1) {
                                messageBuilder.append(" ");
                            }
                            messageBuilder.append(args[i]);
                        }
                        String message = messageBuilder.toString();

                        // Get formats from config
                        String senderFormat = plugin.getConfig().getString("chat.direct.sender", "&7[&bDirect&7] &aYou &6>> &f%receiver%: %message%");
                        String receiverFormat = plugin.getConfig().getString("chat.direct.receiver", "&7[&bDirect&7] &a%sender% &6>> &fYou: %message%");

                        // Format messages
                        String senderMessage = ChatColor.translateAlternateColorCodes('&', senderFormat
                                .replace("%receiver%", target.getName())
                                .replace("%message%", message));
                        String receiverMessage = ChatColor.translateAlternateColorCodes('&', receiverFormat
                                .replace("%sender%", player.getName())
                                .replace("%message%", message));

                        // Send formatted messages
                        player.sendMessage(senderMessage);
                        target.sendMessage(receiverMessage);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /m [nickname] [message]");
                }
                return true;
            }

            if (args.length == 2) {
                if (command.getName().equalsIgnoreCase("distance")) {
                    try {
                        int distance = Integer.parseInt(args[1]);
                        plugin.getConfig().set("chat.local.distance", distance);
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Local chat distance set to " + distance);
                        return true;
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid distance value.");
                        return false;
                    }
                } else if (command.getName().equalsIgnoreCase("globalFormat")) {
                    String format = args[1];
                    plugin.getConfig().set("chat.global.format", format);
                    plugin.saveConfig();
                    player.sendMessage(ChatColor.GREEN + "Global chat format updated.");
                    return true;
                } else if (command.getName().equalsIgnoreCase("localFormat")) {
                    String format = args[1];
                    plugin.getConfig().set("chat.local.format", format);
                    plugin.saveConfig();
                    player.sendMessage(ChatColor.GREEN + "Local chat format updated.");
                    return true;
                }
            }

            player.sendMessage(ChatColor.RED + "Unknown command. Use /help for a list of commands.");
        }
        return false;
    }
}
