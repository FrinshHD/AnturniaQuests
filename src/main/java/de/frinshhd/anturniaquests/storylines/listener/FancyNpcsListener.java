package de.frinshhd.anturniaquests.storylines.listener;

import de.frinshhd.anturniaquests.Main;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FancyNpcsListener implements Listener {

    public FancyNpcsListener(boolean notGenerated) {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }


    @EventHandler
    public void onNpcInteract(NpcInteractEvent event) {
        String npcID = event.getNpc().getData().getName();

        Main.getStorylinesManager().npcClick(event.getPlayer(), npcID);
    }

}
