import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class globalFormat implements CommandExecutor {

    private final Chatter plugin;

    public globalFormat(Chatter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (sender.hasPermission("chatterx.format")) {
                String format = String.join(" ", args);

                if (!format.isEmpty()) {
                    plugin.setGlobalChatFormat(format); // Updated method name
                    sender.sendMessage(ChatColor.GREEN + "Global chat format set.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Format cannot be empty.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /globalFormat [format]");
        }
        return true;
    }
}
