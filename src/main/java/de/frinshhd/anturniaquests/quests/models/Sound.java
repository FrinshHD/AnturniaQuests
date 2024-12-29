package de.frinshhd.anturniaquests.quests.models;

import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;

public class Sound {

    @SerializedName("sound")
    private org.bukkit.Sound sound = null;

    @SerializedName("volume")
    private Float volume = 50F;

    @SerializedName("pitch")
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