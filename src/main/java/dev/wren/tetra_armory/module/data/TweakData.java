package dev.wren.tetra_armory.module.data;

import com.google.common.collect.Multimap;
import dev.wren.tetra_armory.armor.ArmorHitStat;
import dev.wren.tetra_armory.effect.ArmorEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import se.mickelus.tetra.module.data.ItemProperties;
import se.mickelus.tetra.properties.AttributeHelper;

public class TweakData {
    private final VariantData properties = new VariantData();
    public String variant;
    public String improvement;
    public String key;
    public int steps;

    public ItemProperties getProperties(int step) {
        return this.properties.multiply((float)step);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(int step) {
        return AttributeHelper.multiplyModifiers(this.properties.attributes, step);
    }

    public ArmorData getArmorData(int step) {
        return ArmorData.multiply(this.properties.armor, (float) step, (float) step, (float) step);
    }

    public EffectData getEffectData(int step) {
        return EffectData.multiply(this.properties.effects, (float) step, (float) step, (float) step);
    }

    public int getEffectDefense(ArmorEffect effect, int step) {
        return step * this.properties.effects.getDefense(effect);
    }

    public int getArmorDefense(ArmorHitStat armor, int step) {
        return step * this.properties.armor.getDefense(armor);
    }
}
