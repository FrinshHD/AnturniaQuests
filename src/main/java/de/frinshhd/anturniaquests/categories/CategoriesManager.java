package de.frinshhd.anturniaquests.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.categories.models.Category;
import org.bukkit.ChatColor;

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
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType mapTypeCategories = typeFactory.constructMapType(LinkedHashMap.class, String.class, Category.class);

        try {
            this.categories = mapper.readValue(new FileInputStream("plugins/AnturniaQuests/categories.yml"), mapTypeCategories);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading categories.yml. AnturniaQuests will be disabled!");
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
