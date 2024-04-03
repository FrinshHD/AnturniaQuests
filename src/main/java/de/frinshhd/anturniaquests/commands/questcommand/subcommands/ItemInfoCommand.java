package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemInfoCommand extends BasicSubCommand {

    public ItemInfoCommand() {
        super("quests", "anturniaquests.command.admin.itemInfo", new String[]{"iteminfo"});
        setDescription("Shows information about the item you are holding in your main hand.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, new String[]{});
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().equals(Material.AIR)) {
            player.sendMessage("§cYour holding no valid item!");
            return false;
        }

        if (!item.hasItemMeta()) {
            player.sendMessage("§cYour item has no custom attributes!");
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();

        player.sendMessage("§2material: §r" + item.getType());
        player.sendMessage("§2amount: §r" + item.getAmount());

        assert itemMeta != null;
        if (itemMeta.hasDisplayName()) {
            String displayName = itemMeta.getDisplayName();
            displayName = displayName.replace('§', '&');
            player.sendMessage("§2displayName: §r" + displayName);
        }

        if (itemMeta.hasLore()) {
            ArrayList<String> loreRaw = new ArrayList<>(Objects.requireNonNull(itemMeta.getLore()));
            ArrayList<String> lore = new ArrayList<>();
            loreRaw.forEach(string -> {
                string = string.replace('§', '&');
                lore.add(string);
            });

            player.sendMessage("§2lore:");

            lore.forEach(string -> {
                player.sendMessage("§7- §r" + string);
            });
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("iteminfo");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 0) {
                completions.add(completion);
                return;
            }

            if (args.length == 1 && completion.toLowerCase().startsWith(args[0])) {
                completions.add(completion);
                return;
            }
        });

        return completions;
    }

}
