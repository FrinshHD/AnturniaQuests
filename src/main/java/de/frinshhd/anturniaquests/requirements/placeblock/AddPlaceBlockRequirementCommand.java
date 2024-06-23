package de.frinshhd.anturniaquests.requirements.placeblock;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddPlaceBlockRequirementCommand extends BasicSubCommand {

    public AddPlaceBlockRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "placeBlock", "<x>", "<y>", "<z>", "<world>", "<material>"});
        setDescription("Add a placeBlock requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 10) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "placeBlock"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        ArrayList<String> locationsRaw = new ArrayList<>(List.of(args[5], args[6], args[7]));
        ArrayList<Integer> locations = new ArrayList<>();

        for (String locationRaw : locationsRaw) {
            if (!isInteger(locationRaw)) {
                ChatManager.sendMessage(sender, "false value");
                return true;
            }

            locations.add(Integer.parseInt(locationRaw));
        }

        String world = args[8];

        if (Bukkit.getWorld(world) == null) {
            ChatManager.sendMessage(sender, "world does not exist");
            return true;
        }

        String material = args[9];

        if (Material.getMaterial(material.toUpperCase()) == null) {
            ChatManager.sendMessage(sender, "material" + material + " does not exist");
            return true;
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        PlaceBlockModel placeBlockModel = new PlaceBlockModel(new LinkedHashMap<>());
        placeBlockModel.setLocation(locations);
        placeBlockModel.setWorld(world);
        placeBlockModel.setMaterial(Material.valueOf(material.toUpperCase()));

        quest.addRequirement("placeBlocks", placeBlockModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.add.requirement.placeBlock", new TranslatorPlaceholder("questID", questID)));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("placeBlock");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
                return;
            }
        });

        if (args.length == 9) {
            Bukkit.getWorlds().forEach(world -> {
                String worldName = world.getName();
                if (worldName.toLowerCase().startsWith(args[8].toLowerCase())) {
                    completions.add(worldName.toLowerCase());
                }
            });
        }

        if (args.length == 10) {
            for (Material material : Material.values()) {
                String materialName = material.name();
                if (materialName.toLowerCase().startsWith(args[9].toLowerCase())) {
                    completions.add(materialName.toLowerCase());
                }
            }
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
