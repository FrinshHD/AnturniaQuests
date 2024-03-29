package de.frinshhd.anturniaquests.commands;

import de.frinshhd.anturniaquests.quests.models.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    //main command; BasicSubCommand
    Map<String, BasicSubCommand> subCommands = new HashMap<>();

    public CommandManager() {

    }

}
