import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Help implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("chatterx.help")) { // Check permission
                // Send a list of commands to the player
                player.sendMessage("§aChatterX Commands:");
                player.sendMessage("§7----------------------");
                player.sendMessage("§e/reload §7- §fReloads the ChatterX plugin");
                player.sendMessage("§e/help §7- §fShows all commands available");
                player.sendMessage("§e/distance [amount] §7- §fAmount of blocks set for Local Chat");
                player.sendMessage("§7----------------------");
                player.sendMessage("§e/localFormat [text] §f- §fChange appearance of Local Chat");
                player.sendMessage("§e/globalFormat [text] §f- §fChange appearance of Global Chat");
                player.sendMessage("§7----------------------");
            } else {
                player.sendMessage("§cYou do not have permission!");
            }
        } else {
            // If the sender is not a player, print to console
            System.out.println("§aChatterX Commands:");
            System.out.println("§e/reload §7- §fReloads the ChatterX plugin");
            System.out.println("§e/help §7- §fShows this help message");
        }
        return true;
    }
}
