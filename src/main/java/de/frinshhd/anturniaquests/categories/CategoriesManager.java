package de.frinshhd.anturniaquests.categories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.categories.models.Category;
import org.bukkit.ChatColor;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CategoriesManager {

    public LinkedHashMap<String, Category> categories;

    public CategoriesManager() {
        categories = new LinkedHashMap<>();
        load();
    }

    /**
     * Search and register quests
     */
    /**
     * Search and register quests
     */
    public void load() {
        Yaml yaml = Main.getYaml();
        Gson gson = Main.getGson();

        try (FileInputStream inputStream = new FileInputStream("plugins/AnturniaQuests/categories.yml")) {
            LinkedHashMap<String, Object> yamlData = yaml.load(inputStream);
            String jsonString = gson.toJson(yamlData);
            this.categories = gson.fromJson(jsonString, new TypeToken<LinkedHashMap<String, Category>>(){}.getType());
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading categories.yml. AnturniaQuests will be disabled!\nError " + e.getMessage());
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    public Category getCategory(String categoryID) {
        return this.categories.get(categoryID);
    }

    public String getCategoryID(Category category) {

        for (Map.Entry<String, Category> stringCategoryEntry : categories.entrySet()) {
            if (stringCategoryEntry.getValue().equals(category)) {
                return stringCategoryEntry.getKey();
            }
        }

        return null;
    }
}
