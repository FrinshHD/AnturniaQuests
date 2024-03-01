package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.entity.Player;

public class Sound {

    @JsonProperty
    private org.bukkit.Sound sound = null;

    @JsonProperty
    private Float volume = 50F;

    @JsonProperty
    private Float pitch = 1F;

    public org.bukkit.Sound getSound() {
        return this.sound;
    }

    public float getVolume() {
        if (this.volume == null) {
            return 0;
        }

        return this.volume;
    }

    public float getPitch() {
        if (this.pitch == null) {
            return 0;
        }

        return this.pitch;
    }

    public void playSound(Player player) {
        if (getSound() == null) {
            return;
        }

        player.playSound(player.getLocation(), getSound(), getVolume(), getPitch());
    }

}
