package dev.wren.tetra_armory.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArmorTierData<T> {

    public Map<T, Float> defenseMap;
    public Map<T, Float> toughnessMap;
    public Map<T, Float> kbResMap;

    public ArmorTierData() {
        defenseMap = new HashMap<>();
        toughnessMap = new HashMap<>();
        kbResMap = new HashMap<>();
    }

    public boolean contains(T key) {
        return defenseMap.containsKey(key);
    }

    public int getDefense(T key) {
        if (contains(key)) {
            return Math.round(defenseMap.get(key));
        }
        return 0;
    }

    public Map<T, Integer> getLevelMap() {
        return defenseMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Math.round(e.getValue())));
    }

    public float getToughness(T key) {
        if (toughnessMap.containsKey(key)) {
            return toughnessMap.get(key);
        }
        return 0;
    }

    public float getKbRes(T key) {
        if (kbResMap.containsKey(key)) {
            return kbResMap.get(key);
        }
        return 0;
    }

    public Set<T> getValues() {

        Stream<Map.Entry<T, Float>> defenseAndToughnessStream = Stream.concat(
                defenseMap.entrySet().stream().filter(entry -> entry.getValue() > 0),
                toughnessMap.entrySet().stream().filter(entry -> entry.getValue() > 0)
        ); // yeah idk i can't think of any better way to do this

        return Stream.concat(
                        defenseAndToughnessStream,
                        kbResMap.entrySet().stream().filter(entry -> entry.getValue() > 0)
                )
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());

    }

}
