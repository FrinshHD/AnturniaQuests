package de.frinshhd.anturniaquests.utils;

import com.google.gson.internal.LinkedTreeMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapConverter {
    public static <K, V> LinkedHashMap<K, V> convertLinkedTreeMapToLinkedHashMap(LinkedTreeMap<K, V> linkedTreeMap) {
        return new LinkedHashMap<>(linkedTreeMap);
    }
}