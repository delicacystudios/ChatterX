import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final Chatter plugin;
    private List<String> blacklistWords;
    private boolean blacklistEnabled;
    private boolean replaceEnabled;
    private String blacklistMessage;
    private String replacementMessage;
    private int localChatDistance;

    private static final Pattern HEX_COLOR_PATTERN_HASH = Pattern.compile("(?<!&)#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_COLOR_PATTERN_AMP = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("(?<!&)§([0-9a-fk-or])");

    public ChatListener(ChatCommandExecutor chatCommandExecutor, Chatter plugin, boolean isLuckPermsAvailable) {
        this.plugin = plugin;
        reloadConfigValues();
    }

    private void reloadConfigValues() {
        this.blacklistEnabled = plugin.isBlacklistEnabled();
        this.blacklistWords = plugin.getBlacklistWords();
        this.replaceEnabled = plugin.isReplaceEnabled();
        this.blacklistMessage = plugin.getBlacklistMessage();
        this.replacementMessage = plugin.getReplacementMessage();
        this.localChatDistance = plugin.getLocalChatDistance();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Apply colors based on permissions
        if (player.hasPermission("chat.hex")) {
            message = applyHexColors(message);
        }
        if (player.hasPermission("chat.legacy")) {
            message = applyLegacyColors(message);
        }

        // Handle Blacklist
        if (blacklistEnabled) {
            boolean containsBlacklistedWord = false;
            String originalMessage = message;

            if (replaceEnabled) {
                // Replace each blacklisted word with replacementMessage
                for (String word : blacklistWords) {
                    // Create a case-insensitive pattern for word replacement
                    String wordPattern = "(?i)" + Pattern.quote(word);
                    Matcher matcher = Pattern.compile(wordPattern).matcher(message);

                    // Replace all occurrences of the blacklisted word
                    if (matcher.find()) {
                        containsBlacklistedWord = true;
                        message = matcher.replaceAll(Matcher.quoteReplacement(replacementMessage));
                    }
                }

                // If any word was replaced, set the message and proceed
                if (containsBlacklistedWord) {
                    event.setMessage(message);
                }
            } else {
                // If replaceEnabled is false, handle blacklisted words by replacing the whole message
                for (String word : blacklistWords) {
                    if (message.toLowerCase().contains(word.toLowerCase())) {
                        containsBlacklistedWord = true;
                        break;
                    }
                }

                if (containsBlacklistedWord) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', blacklistMessage));
                    return;
                }
            }
        }

        // Determine if the message is global or local
        if (message.startsWith("!")) {
            // Global Chat
            message = message.substring(1); // Remove the "!" prefix
            event.setFormat(ChatColor.translateAlternateColorCodes('&', plugin.getGlobalChatFormat())
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        } else {
            // Local Chat
            event.setCancelled(true); // Cancel the original event to handle local chat manually

            String localMessage = ChatColor.translateAlternateColorCodes('&', plugin.getLocalChatFormat())
                    .replace("%player%", player.getName())
                    .replace("%message%", message);

            for (Player recipient : event.getRecipients()) {
                if (recipient.getWorld().equals(player.getWorld()) &&
                        recipient.getLocation().distance(player.getLocation()) <= localChatDistance) {
                    recipient.sendMessage(localMessage);
                }
            }
        }
    }

    public static String applyHexColors(String text) {
        Matcher matcher = HEX_COLOR_PATTERN_HASH.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(sb);

        text = sb.toString();
        matcher = HEX_COLOR_PATTERN_AMP.matcher(text);
        sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static String applyLegacyColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
