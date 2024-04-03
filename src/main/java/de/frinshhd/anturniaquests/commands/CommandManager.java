package de.frinshhd.anturniaquests.commands;

import de.frinshhd.anturniaquests.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager {


    //command name; command
    private Map<String, BasicCommand> commands = new HashMap<>();

    //main command; list of BasicSubCommands
    private Map<BasicCommand, List<BasicSubCommand>> subCommands = new HashMap<>();

    public CommandManager() {
        //Todo: execute load function
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

                        commands.put(basicCommand.getName(), basicCommand);

                        try {
                            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                            bukkitCommandMap.setAccessible(true);
                            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                            commandMap.register(Main.getInstance().getName(), basicCommand);
                        } catch(Exception e) {
                            Main.getInstance().getLogger().warning("[DynamicCommands] Error registering command " + className + " " + e);
                            return;
                        }

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
        List<BasicSubCommand> subCommands = new ArrayList<>();

        for (BasicSubCommand subCommand : getSubCommands(command)) {
            if (subCommand.getPath()[0].startsWith(subCommandPath[0])) {
                subCommands.add(subCommand);
            }
        }

        System.out.println(Arrays.toString(subCommandPath));
        System.out.println(subCommands);


        int index = 1;
        while (subCommands.size() > 1) {
            for (BasicSubCommand subCommand : subCommands) {
                if (subCommands.size() == 1) {
                    break;
                }

                if (subCommand.getPath().length <= index) {
                    subCommands.remove(subCommand);
                    continue;
                }

                if (!subCommand.getPath()[index].startsWith(subCommandPath[index])) {
                    subCommands.remove(subCommand);
                }
            }
            
            index++;
        }
        
        if (subCommands.size() == 1) {
            return subCommands.get(0);
        }


        return null;
    }

}
