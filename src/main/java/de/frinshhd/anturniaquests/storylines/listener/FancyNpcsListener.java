package de.frinshhd.anturniaquests.storylines.listener;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.storylines.models.NPC;
import de.frinshhd.anturniaquests.storylines.models.Storyline;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FancyNpcsListener implements Listener {

    public FancyNpcsListener(boolean notGenerated) {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }


    @EventHandler
    public void onNpcInteract(NpcInteractEvent event) {
        String npcID = event.getNpc().getData().getId();
        NPC npc = null;

        for (Storyline storyline : Main.getStorylinesManager().storylines.values()) {
            if (storyline.getNPC(npcID) != null) {
                npc = storyline.getNPC(npcID);
            }
        }

        if (npc == null) {
            return;
        }

        Main.getStorylinesManager().npcClick(event.getPlayer(), npc.getNpcID());
    }
}
