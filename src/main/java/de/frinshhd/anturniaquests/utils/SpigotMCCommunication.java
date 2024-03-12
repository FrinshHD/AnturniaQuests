package de.frinshhd.anturniaquests.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.frinshhd.anturniaquests.Main;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpigotMCCommunication {

    public static void init() {
        Main.version = checkForNewVersion();
        Main.getInstance().getLogger().info("You are running on version: " + Main.getInstance().getDescription().getVersion());
    }

    public static String checkForNewVersion() {
        String currentVersion = Main.getInstance().getDescription().getVersion();

        try {
            String latestVersion = getLatestReleaseVersion();

            if (latestVersion.toLowerCase().startsWith("beta-")) {
                return currentVersion;
            }

            ComparableVersion latestComparableVersion = new ComparableVersion(latestVersion);
            ComparableVersion currentComparableVersion = new ComparableVersion(currentVersion);

            int compare = currentComparableVersion.compareTo(latestComparableVersion);
            System.out.println(compare);

            if (compare < 0) {
                return latestVersion;
            }

            return currentVersion;
        } catch (NullPointerException | IOException e) {
            Main.getInstance().getLogger().warning("The plugin wasn't able to check for updates! Please check your internet connection.");
        }

        return null;
    }

    private static String getLatestReleaseVersion() throws IOException {
        String apiUrl = "https://api.spiget.org/v2/resources/113784/updates/latest?size=1";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();

        if (jsonResponse.has("title")) {
            return jsonResponse.get("title").getAsString();
        }

        return null;
    }

}
