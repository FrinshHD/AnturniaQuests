package de.frinshhd.anturniaquests.config.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.config.ConfigManager;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;

import java.util.Objects;

public class QuestMenu {

    @JsonProperty
    private boolean enabled = true;

    @JsonProperty
    private ItemModel forwardItem = new ItemModel();

    @JsonProperty
    private ItemModel backwardItem = new ItemModel();

    @JsonIgnore
    public boolean isEnabled() {
        return Objects.requireNonNullElseGet(Main.getConfigManager().getConfig().questMenuEnabled, () -> enabled);

    }

    @JsonIgnore
    public ItemModel getForwardItem() {
        return forwardItem;
    }

    @JsonIgnore
    public ItemModel getBackwardItem() {
        return backwardItem;
    }

}
