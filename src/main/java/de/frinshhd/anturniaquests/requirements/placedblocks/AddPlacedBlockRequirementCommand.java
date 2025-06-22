package de.frinshhd.anturniaquests.requirements.placedblocks;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class AddPlacedBlockRequirementCommand extends BasicSubCommand {

    public AddPlacedBlockRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "placedBlocks", "<material>", "<amount>", "[worlds]"});
        setDescription("Add a placedBlocks requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 7) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "placedBlocks"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.dontExists", new Translatable("questID", questID)));
            return true;
        }

        String material = args[5];

        if (Material.getMaterial(material.toUpperCase()) == null) {
            ChatManager.sendMessage(sender, "material" + material + " does not exist");
            return true;
        }

        String amount = args[6];

        if (!isInteger(amount)) {
            ChatManager.sendMessage(sender, "false value");
            return true;
        }

        ArrayList<String> worlds = new ArrayList<>();

        if (args.length > 7) {
            worlds.addAll(Arrays.asList(args).subList(7, args.length));
        }

        for (String world : worlds) {
            if (Bukkit.getWorld(world) == null) {
                ChatManager.sendMessage(sender, "world" + world + " does not exist");
                return true;
            }
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        PlacedBlocksModel placedBlocksModel = new PlacedBlocksModel(new LinkedHashMap<>());
        placedBlocksModel.setMaterial(Material.getMaterial(material.toUpperCase()));
        placedBlocksModel.setAmount(Integer.parseInt(amount));

        if (!worlds.isEmpty()) {
            placedBlocksModel.setWorlds(worlds);
        }

        quest.addRequirement("placedBlocks", placedBlocksModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.command.edit.add.requirement.placedBlocks", new Translatable("questID", questID), new Translatable("amount", String.valueOf(placedBlocksModel.getAmount())), new Translatable("material", placedBlocksModel.getDisplayName())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("placedBlocks");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        if (args.length == 6) {
            for (Material material : Material.values()) {
                String materialName = material.name();
                if (materialName.toLowerCase().startsWith(args[5].toLowerCase())) {
                    completions.add(materialName.toLowerCase());
                }
            }
        }

        if (args.length >= 8) {
            Bukkit.getWorlds().forEach(world -> {
                String worldName = world.getName();
                if (worldName.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    completions.add(worldName.toLowerCase());
                }
            });
        }


        return completions;
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
