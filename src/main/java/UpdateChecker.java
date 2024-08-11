import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final int resourceId;
    private final String currentVersion;

    public UpdateChecker(JavaPlugin plugin, int resourceId, String currentVersion) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.currentVersion = currentVersion;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String latestVersion = reader.readLine();
            reader.close();

            if (!currentVersion.equals(latestVersion)) {
                Bukkit.getLogger().info("A new version of the plugin is available: " + latestVersion);
                // Optionally, send notifications to server admins or show a message in-game
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}