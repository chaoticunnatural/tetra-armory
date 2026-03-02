package dev.wren.tetra_armory.module.data;

import com.mojang.datafixers.util.Pair;

import java.util.*;
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

    protected static <D extends ArmorTierData<?>> D defaultOverwrite(D a, D b, D result) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }
        result.defenseMap.putAll(a.defenseMap);
        result.toughnessMap.putAll(a.toughnessMap);
        result.kbResMap.putAll(a.kbResMap);

        result.defenseMap.putAll(b.defenseMap);
        result.toughnessMap.putAll(b.toughnessMap);
        result.kbResMap.putAll(b.kbResMap);

        return result;
    }

    protected static <D extends ArmorTierData<?>> D defaultMerge(D a, D b, D result) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }

        result.defenseMap = Stream.of(a, b)
                .map(armorData -> armorData.defenseMap)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Float::sum));
        result.toughnessMap = Stream.of(a, b)
                .map(armorData -> armorData.toughnessMap)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Float::sum));
        result.kbResMap = Stream.of(a, b)
                .map(armorData -> armorData.kbResMap)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Float::sum));

        return result;
    }

    protected static <D extends ArmorTierData<?>> D defaultMultiply(float defenseMultiplier, float toughnessMultiplier, float kbResModifier, D base, D result) {
        return Optional.ofNullable(base)
                .map(data -> {
                    result.defenseMap = data.defenseMap.entrySet().stream()
                            .map(entry -> new Pair<>(entry.getKey(), entry.getValue() * defenseMultiplier))
                            .filter(pair -> pair.getSecond() != 0)
                            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
                    result.toughnessMap = data.toughnessMap.entrySet().stream()
                            .map(entry -> new Pair<>(entry.getKey(), entry.getValue() * toughnessMultiplier))
                            .filter(pair -> pair.getSecond() != 0)
                            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
                    result.kbResMap = data.kbResMap.entrySet().stream()
                            .map(entry -> new Pair<>(entry.getKey(), entry.getValue() * kbResModifier))
                            .filter(pair -> pair.getSecond() != 0)
                            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
                    return result;
                })
                .orElse(null);
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
