package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.add.rewards;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Command;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.quests.models.Rewards;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCommandRewardCommand extends BasicSubCommand {

    public AddCommandRewardCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.reward", new String[]{"edit", "<questID>", "add", "reward", "command", "<name>", "<command>"});
        setDescription("Add a command reward to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 7) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "reward", "command"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.dontExists", new Translatable("questID", questID)));
            return true;
        }

        String name = args[5];

        if (name.equals("none")) {
            name = null;
        }

        StringBuilder commands = new StringBuilder();

        Arrays.asList(args).subList(6, args.length).forEach(command -> {
            commands.append(command).append(" ");
        });

        commands.delete(commands.length() - 1, commands.length());

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        Rewards rewards = quest.getRewards();

        Command command = new Command();
        command.setCommand(commands.toString());

        if (name != null) {
            command.setName(name);
        }

        rewards.addCommand(command);

        quest.setRewards(rewards);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.command.edit.add.reward.command", new Translatable("questID", questID), new Translatable("commandName", command.getName())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("command");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        if (args.length == 6) {
            ArrayList<String> possible = new ArrayList<>(List.of("none"));
            possible.forEach(completion -> {
                if (completion.startsWith(args[5].toLowerCase())) {
                    completions.add(completion);
                }
            });
        }

        return completions;
    }
}
