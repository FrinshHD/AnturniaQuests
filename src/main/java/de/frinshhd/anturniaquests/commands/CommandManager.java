package de.frinshhd.anturniaquests.commands;

import de.frinshhd.anturniaquests.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager {


    //command name; command
    private Map<String, BasicCommand> commands = new HashMap<>();

    //main command; list of BasicSubCommands
    private Map<BasicCommand, List<BasicSubCommand>> subCommands = new HashMap<>();

    public CommandManager() {

    }

    public void load(Set<String> classNames, String canonicalName) {
        loadMainCommands(classNames, canonicalName);
        loadSubCommands(classNames, canonicalName);

        //sort subcommands alphabetically
        subCommands.forEach((command, subCommands) -> {
            subCommands.sort(Comparator.comparing(BasicSubCommand::getCommand));
            this.subCommands.put(command, subCommands);
        });
    }

    public void loadMainCommands(Set<String> classNames, String canonicalName) {
        Iterator<String> classNamesIterator = classNames.iterator();
        while (classNamesIterator.hasNext()) {
            String className = classNamesIterator.next();

            if (className.contains(canonicalName)) {
                try {
                    Class<?> cls = Class.forName(className);

                    Class<BasicCommand> basicCommandClass = BasicCommand.class;

                    if (basicCommandClass.isAssignableFrom(cls)) {
                        Constructor<?> constructor = cls.getConstructors()[0];

                        Main.getInstance().getLogger().info("[DynamicCommands] Trying to register command " + className);

                        BasicCommand basicCommand = (BasicCommand) constructor.newInstance();

                        commands.put(basicCommand.getCommand(), basicCommand);

                        Main.getInstance().getLogger().info("[DynamicCommands] Successfully registered command " + className);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | IllegalArgumentException e) {
                    Main.getInstance().getLogger().warning("[DynamicCommands] Error loading command " + className + " " + e);
                }
            }
        }
    }

    public void loadSubCommands(Set<String> classNames, String canonicalName) {
        Iterator<String> classNamesIterator = classNames.iterator();
        while (classNamesIterator.hasNext()) {
            String className = classNamesIterator.next();

            if (className.contains(canonicalName)) {
                try {
                    Class<?> cls = Class.forName(className);

                    Class<BasicSubCommand> basicSubCommandClass = BasicSubCommand.class;

                    if (basicSubCommandClass.isAssignableFrom(cls)) {
                        Constructor<?> constructor = cls.getConstructors()[0];

                        Main.getInstance().getLogger().info("[DynamicCommands] Trying to register subcommand " + className);

                        BasicSubCommand basicSubCommand = (BasicSubCommand) constructor.newInstance();

                        BasicCommand command = getCommand(basicSubCommand.getMainCommand());

                        if (!this.subCommands.containsKey(command)) {
                            this.subCommands.put(command, new ArrayList<>(List.of(basicSubCommand)));
                        } else {
                            List<BasicSubCommand> subCommands = getSubCommands(command);
                            subCommands.add(basicSubCommand);

                            this.subCommands.put(command, subCommands);
                        }

                        Main.getInstance().getLogger().info("[DynamicCommands] Successfully registered subcommand " + className);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | IllegalArgumentException e) {
                    Main.getInstance().getLogger().warning("[DynamicCommands] Error loading subcommand " + className + " " + e);
                }
            }
        }
    }


    public List<BasicSubCommand> getSubCommands(BasicCommand command) {
        return subCommands.get(command);
    }

    public BasicCommand getCommand(String name) {
        return commands.get(name);
    }

    public BasicSubCommand getSubCommand(BasicCommand command, String... subCommandPath) {
        for (BasicSubCommand subCommand : getSubCommands(command)) {
            if (Arrays.equals(subCommand.getPath(), subCommandPath)) {
                return subCommand;
            }
        }

        return null;
    }

}
