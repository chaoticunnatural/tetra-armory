package dev.wren.tetra_armory.armor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorHitStat {
    private static final Map<String, ArmorHitStat> hitStats = new ConcurrentHashMap();
    private final String name;

    public static Collection<ArmorHitStat> getHitStats() {
        return Collections.unmodifiableCollection(hitStats.values());
    }

    public static ArmorHitStat get(String name) {
        return hitStats.computeIfAbsent(name, ArmorHitStat::new);
    }

    public static ArmorHitStat helmet(String name) {
        return hitStats.computeIfAbsent("helmet_" + name, ArmorHitStat::new);
    }

    public static ArmorHitStat chestplate(String name) {
        return hitStats.computeIfAbsent("chestplate_" + name, ArmorHitStat::new);
    }

    public static ArmorHitStat leggings(String name) {
        return hitStats.computeIfAbsent("leggings_" + name, ArmorHitStat::new);
    }

    public static ArmorHitStat boots(String name) {
        return hitStats.computeIfAbsent("boots_" + name, ArmorHitStat::new);
    }

    public String name() {
        return this.name;
    }

    public String toString() {
        return "ArmorHitStat[" + this.name + "]";
    }

    private ArmorHitStat(String name) {
        this.name = name;
    }
}
