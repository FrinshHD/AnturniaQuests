package de.frinshhd.anturniaquests;

import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import static org.bukkit.Bukkit.getServer;

public class DynamicListeners {

    public static void load(Iterator<String> classNamesIterator, String canonicalName) {
        while (classNamesIterator.hasNext()) {
            String className = classNamesIterator.next();

            if (className.contains(canonicalName)) {
                try {
                    Class<?> cls = Class.forName(className);

                    Class<Listener> listenerClass = Listener.class;

                    if (listenerClass.isAssignableFrom(cls)) {
                        Constructor<?> constructor = cls.getConstructors()[0];
                        Listener listener = (Listener) constructor.newInstance();

                        getServer().getPluginManager().registerEvents(listener, Main.getInstance());
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | IllegalArgumentException e) {
                    Main.getInstance().getLogger().warning("Error loading listeners in class " + className + " " + e);
                }
            }
        }
    }
}

