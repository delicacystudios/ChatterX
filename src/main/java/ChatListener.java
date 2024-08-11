import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[^%]+%"); // Matches %variable%
    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("(&[0-9a-fk-or])"); // Matches legacy color codes

    private final ChatCommandExecutor chatCommandExecutor;
    private final Chatter plugin;
    private final boolean isLuckPermsAvailable;

    public ChatListener(ChatCommandExecutor chatCommandExecutor, Chatter plugin, boolean isLuckPermsAvailable) {
        this.chatCommandExecutor = chatCommandExecutor;
        this.plugin = plugin;
        this.isLuckPermsAvailable = isLuckPermsAvailable;
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        boolean isGlobal = message.startsWith("!");

        if (isGlobal) {
            message = message.substring(1).trim();
            chatCommandExecutor.setGlobalChat(player, true);
        } else {
            chatCommandExecutor.setGlobalChat(player, false);
        }

        // Handle direct messages
        if (message.startsWith("/m ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length == 3) {
                Player target = Bukkit.getPlayer(parts[1]);
                if (target != null) {
                    String senderFormat = plugin.getConfig().getString("chat.direct.sender", "&7[&bDirect&7] &aYou &6>> &f%receiver%: %message%");
                    String receiverFormat = plugin.getConfig().getString("chat.direct.receiver", "&7[&bDirect&7] &a%sender% &6>> &fYou: %message%");

                    String senderMessage = sanitizeMessage(senderFormat
                            .replace("%receiver%", target.getName())
                            .replace("%message%", parts[2]));
                    String receiverMessage = sanitizeMessage(receiverFormat
                            .replace("%sender%", player.getName())
                            .replace("%message%", parts[2]));

                    player.sendMessage(applyColors(senderMessage));
                    target.sendMessage(applyColors(receiverMessage));
                    event.setCancelled(true);
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /m [nickname] [message]");
            }
        } else {
            // Handle global and local chat formatting
            String formatKey = isGlobal ? "chat.global.format" : "chat.local.format";
            String format = plugin.getConfig().getString(formatKey, "<%player%> %message%");
            String formattedMessage = sanitizeMessage(message);

            // Apply LuckPerms prefixes/suffixes if available
            if (isLuckPermsAvailable) {
                UserManager userManager = LuckPermsProvider.get().getUserManager();
                User user = userManager.getUser(player.getUniqueId());
                if (user != null) {
                    String prefix = user.getCachedData().getMetaData().getPrefix();
                    String suffix = user.getCachedData().getMetaData().getSuffix();
                    prefix = applyColors(prefix != null ? prefix : "");
                    suffix = applyColors(suffix != null ? suffix : "");
                    format = format.replace("%prefix%", prefix)
                            .replace("%suffix%", suffix);
                }
            } else {
                format = format.replace("%prefix%", "").replace("%suffix%", "");
            }

            format = format.replace("%player%", player.getDisplayName()).replace("%message%", formattedMessage);
            format = applyColors(format);

            if (isGlobal) {
                event.setFormat(format);
            } else {
                int distance = plugin.getConfig().getInt("chat.local.distance", 50);
                Set<Player> recipients = new HashSet<>();
                Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getWorld().equals(player.getWorld()) && p.getLocation().distance(player.getLocation()) < distance)
                        .forEach(recipients::add);

                event.getRecipients().clear();
                event.getRecipients().addAll(recipients);

                // Add hover and click actions to player names
                String[] words = format.split(" ");
                for (int i = 0; i < words.length; i++) {
                    Player targetPlayer = Bukkit.getPlayer(words[i]);
                    if (targetPlayer != null) {
                        TextComponent nameComponent = new TextComponent(words[i]);
                        nameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Send a direct message")));
                        nameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/m " + words[i] + " "));
                        words[i] = nameComponent.toLegacyText();
                    }
                }
                format = String.join(" ", words);
                event.setFormat(format);
            }
        }
    }

    private String applyColors(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Apply HEX colors
        text = applyHexColors(text);

        // Apply legacy colors
        text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    private String applyHexColors(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        int lastEnd = 0;
        while (matcher.find()) {
            sb.append(text, lastEnd, matcher.start());

            String hexColor = matcher.group(1);
            sb.append("§x");
            for (char c : hexColor.toCharArray()) {
                sb.append("§").append(c);
            }

            lastEnd = matcher.end();
        }
        sb.append(text.substring(lastEnd));

        return sb.toString();
    }

    private String sanitizeMessage(String message) {
        if (message == null || message.contains("%")) {
            return "";
        }
        // Remove unsupported placeholders
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        return matcher.replaceAll(""); // Replace any placeholders with an empty string
    }
}

