import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {

    private final Chatter plugin;

    public Reload(Chatter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("chatterx.reload")) { // Check permission
            plugin.reloadConfig();
            sender.sendMessage("§aChatterX plugin reloaded successfully!");
        } else {
            sender.sendMessage("§cYou do not have permission to reload the plugin.");
        }
        return true;
    }
}
