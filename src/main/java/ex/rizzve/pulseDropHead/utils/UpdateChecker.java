package ex.rizzve.pulseDropHead.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker {
    private final JavaPlugin plugin;
    private final String projectId;
    private final String currentVersion;

    public UpdateChecker(JavaPlugin plugin, String projectId) {
        this.plugin = plugin;
        this.projectId = projectId;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void checkForUpdates() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            HttpURLConnection connection = null;

            try {
                try {
                    String apiUrl = "https://api.modrinth.com/v2/project/" + this.projectId + "/version";
                    URL url = new URL(apiUrl);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "HeadDrop/" + this.currentVersion);
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        this.plugin.getLogger().warning("Failed to check for updates : HTTP " + responseCode);
                        return;
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    label180: {
                        try {
                            StringBuilder response = new StringBuilder();

                            String line;
                            while((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            JsonArray versions = JsonParser.parseString(response.toString()).getAsJsonArray();
                            if (versions.size() != 0) {
                                JsonObject latestVersion = versions.get(0).getAsJsonObject();
                                String latestVersionNumber = latestVersion.get("version_number").getAsString();
                                String var10000 = this.projectId;
                                String downloadUrl = "https://modrinth.com/plugin/" + var10000 + "/version/" + latestVersion.get("id").getAsString();
                                if (this.isNewerVersion(latestVersionNumber, this.currentVersion)) {
                                    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                                        this.plugin.getLogger().info("A new version of PulseHeadDrop is available !");
                                        this.plugin.getLogger().info("Current version : " + this.currentVersion);
                                        this.plugin.getLogger().info("Latest version : " + latestVersionNumber);
                                        this.plugin.getLogger().info("Download : " + downloadUrl);
                                    });
                                } else {
                                    this.plugin.getLogger().info("HeadDrop is up to date ! (v" + this.currentVersion + ")");
                                }
                                break label180;
                            }
                        } catch (Throwable var18) {
                            try {
                                reader.close();
                            } catch (Throwable var17) {
                                var18.addSuppressed(var17);
                            }

                            throw var18;
                        }

                        reader.close();
                        return;
                    }

                    reader.close();
                } catch (Exception var19) {
                    this.plugin.getLogger().log(Level.WARNING, "Failed to check for updates : " + var19.getMessage());
                }

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

            }
        });
    }

    private boolean isNewerVersion(String latest, String current) {
        try {
            String[] latestParts = latest.replaceAll("[^0-9.]", "").split("\\.");
            String[] currentParts = current.replaceAll("[^0-9.]", "").split("\\.");
            int maxLength = Math.max(latestParts.length, currentParts.length);

            for(int i = 0; i < maxLength; ++i) {
                int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                if (latestPart > currentPart) {
                    return true;
                }

                if (latestPart < currentPart) {
                    return false;
                }
            }

            return false;
        } catch (Exception var9) {
            this.plugin.getLogger().warning("Failed to compare versions: " + var9.getMessage());
            return false;
        }
    }
}
