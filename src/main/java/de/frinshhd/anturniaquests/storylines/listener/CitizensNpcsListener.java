package de.frinshhd.anturniaquests.storylines.listener;

import de.frinshhd.anturniaquests.Main;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensNpcsListener implements Listener {

    public CitizensNpcsListener(boolean notGenerated) {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }


    @EventHandler
    public void onNpcInteract(NPCLeftClickEvent event) {
        String npcID = event.getNPC().getName();

        Main.getStorylinesManager().npcClick(event.getClicker(), npcID);
    }

    @EventHandler
    public void onNpcInteract(NPCRightClickEvent event) {
        String npcID = event.getNPC().getName();

        Main.getStorylinesManager().npcClick(event.getClicker(), npcID);
    }

}
