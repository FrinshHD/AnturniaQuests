package de.frinshhd.anturniaquests.utils;

import de.frinshhd.anturniaquests.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

public class DynamicPlaceholderExpansion {

    public static void load(Set<String> classNames, String canonicalName) {
        Iterator<String> classNamesIterator = classNames.iterator();
        while (classNamesIterator.hasNext()) {
            String className = classNamesIterator.next();

            if (className.contains(canonicalName)) {
                try {
                    Class<?> cls = Class.forName(className);

                    Class<PlaceholderExpansion> placeholderExpansionClass = PlaceholderExpansion.class;

                    if (placeholderExpansionClass.isAssignableFrom(cls)) {
                        Constructor<?> constructor = cls.getConstructors()[0];

                        Main.getInstance().getLogger().info("[DynamicPlaceholderExpansion] Trying to register class " + className);

                        PlaceholderExpansion placeholderExpansion = (PlaceholderExpansion) constructor.newInstance();
                        placeholderExpansion.register();

                        Main.getInstance().getLogger().info("[DynamicPlaceholderExpansion] Successfully registered class " + className);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | IllegalArgumentException e) {
                    Main.getInstance().getLogger().warning("Error loading placeholder extension in class " + className + " " + e);
                }
            }
        }
    }

}
