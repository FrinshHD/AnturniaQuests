package de.frinshhd.anturniaquests.utils;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;

public class DynamicListeners {

    public static void load(Set<String> classNames, String canonicalName) {
        for (String className : classNames) {
            if (className.contains(canonicalName)) {
                try {
                    Class<?> cls = Class.forName(className);

                    Class<Listener> listenerClass = Listener.class;

                    if (listenerClass.isAssignableFrom(BasicRequirement.class)) {
                        continue;
                    }

                    if (listenerClass.isAssignableFrom(cls)) {
                        Constructor<?> constructor = cls.getConstructors()[0];
                        Listener listener = (Listener) constructor.newInstance();

                        getServer().getPluginManager().registerEvents(listener, Main.getInstance());
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | IllegalArgumentException | NoClassDefFoundError e) {
                    Main.getInstance().getLogger().warning("Error loading listeners in class " + className + " " + e);
                }
            }
        }
    }
}

