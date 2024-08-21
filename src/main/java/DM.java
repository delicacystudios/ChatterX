import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class DM implements CommandExecutor {

    private JavaPlugin plugin;

    public DM() {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage("Usage: /m <player> <message>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("Player not found.");
            return false;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        message = applyColors(message);

        target.sendMessage(ChatColor.GRAY + "[DM] " + player.getName() + ": " + message);
        player.sendMessage(ChatColor.GRAY + "[DM] To " + target.getName() + ": " + message);

        return true;
    }

    private String applyColors(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // First, apply legacy color codes
        text = ChatColor.translateAlternateColorCodes('&', text);

        // Then, apply HEX colors
        text = ChatListener.applyHexColors(text);

        return text;
    }
}

