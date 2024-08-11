import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class localFormat implements CommandExecutor {

    private final Chatter plugin;

    public localFormat(Chatter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length >= 1) {
            if (sender.hasPermission("chatterx.format")) {
                String format = String.join(" ", args);

                if (!format.isEmpty()) {
                    plugin.setLocalChatFormat(format); // Updated method name
                    sender.sendMessage(ChatColor.GREEN + "Local chat format set.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Format cannot be empty.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /localFormat [format]");
        }
        return true;
    }
}
