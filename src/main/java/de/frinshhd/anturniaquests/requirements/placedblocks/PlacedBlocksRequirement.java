package de.frinshhd.anturniaquests.requirements.placedblocks;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class PlacedBlocksRequirement extends BasicRequirement implements Listener {
    public PlacedBlocksRequirement() {
        super("placedBlocks", false);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        return null;
    }

    @Override
    public Class<?> getModellClass() {
        return PlacedBlocksModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {

    }

    @Override
    public boolean check(Player player, String questID) {
        return false;
    }
}
