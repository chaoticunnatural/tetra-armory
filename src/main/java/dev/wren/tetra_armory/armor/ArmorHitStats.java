package dev.wren.tetra_armory.armor;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArmorHitStats {

    public static final ArmorHitStat HELMET_CRIT = ArmorHitStat.helmet("crit_dmg"); // resistance to extra crit dmg
    public static final ArmorHitStat CHESTPLATE_CRIT = ArmorHitStat.chestplate("crit_dmg");
    public static final ArmorHitStat LEGGINGS_CRIT = ArmorHitStat.leggings("crit_dmg");
    public static final ArmorHitStat BOOTS_CRIT = ArmorHitStat.boots("crit_dmg");
    public static final ArmorHitStat HELMET_KB_RES = ArmorHitStat.helmet("sprint_kb"); // resistance to sprint hit kb
    public static final ArmorHitStat CHESTPLATE_KB_RES = ArmorHitStat.chestplate("sprint_kb");
    public static final ArmorHitStat LEGGINGS_KB_RES = ArmorHitStat.leggings("sprint_kb");
    public static final ArmorHitStat BOOTS_KB_RES = ArmorHitStat.boots("sprint_kb");

    public static final Set<ArmorHitStat> HELMET_HIT_STATS;
    public static final Set<ArmorHitStat> CHESTPLATE_HIT_STATS;
    public static final Set<ArmorHitStat> LEGGINGS_HIT_STATS;
    public static final Set<ArmorHitStat> BOOTS_HIT_STATS;

    private static Set<ArmorHitStat> of(ArmorHitStat... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }

    static {
        HELMET_HIT_STATS = of(HELMET_CRIT);
        CHESTPLATE_HIT_STATS = of(CHESTPLATE_CRIT);
        LEGGINGS_HIT_STATS = of(LEGGINGS_CRIT);
        BOOTS_HIT_STATS = of(BOOTS_CRIT);
    }
}
