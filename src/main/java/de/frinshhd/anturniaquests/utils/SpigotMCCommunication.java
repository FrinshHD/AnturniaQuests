package de.frinshhd.anturniaquests.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.frinshhd.anturniaquests.Main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpigotMCCommunication {

    public static void init() {
        Main.version = checkForNewVersion();
    }

    public static String checkForNewVersion() {
        String currentVersion = Main.getInstance().getDescription().getVersion();

        try {
            String latestVersion = getLatestReleaseVersion();

            if (latestVersion != null && latestVersion.equals(currentVersion)) {
                return currentVersion;
            } else if (latestVersion != null) {
                return latestVersion;
            } else {
                return null;
            }
        } catch (IOException e) {
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
