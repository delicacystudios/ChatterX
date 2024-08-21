import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Distance implements CommandExecutor {

    private final Chatter plugin;

    public Distance(Chatter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                if (sender.hasPermission("chatterx.distance")) {
                    try {
                        int distance = Integer.parseInt(args[0]);
                        plugin.getLocalChatDistance(); // Use updated method name
                        sender.sendMessage(ChatColor.GREEN + "Local chat distance set to " + distance);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid number format.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /distance [# of blocks]");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
        }
        return true;
    }
}
