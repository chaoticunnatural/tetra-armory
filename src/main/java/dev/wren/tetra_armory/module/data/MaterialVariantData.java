package dev.wren.tetra_armory.module.data;

import net.minecraft.resources.ResourceLocation;
import se.mickelus.tetra.module.data.GlyphData;
import se.mickelus.tetra.module.data.ModuleModel;
import se.mickelus.tetra.properties.AttributeHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialVariantData extends VariantData {
    public ResourceLocation[] materials = new ResourceLocation[0];
    public MaterialMultiplier extract = new MaterialMultiplier();

    public VariantData combine(MaterialData material) {
        UniqueVariantData result = new UniqueVariantData();
        result.key = this.key + material.key;
        if (material.category != null) {
            result.category = material.category;
        }

        result.attributes = AttributeHelper.collapseRound(AttributeHelper.merge(Arrays.asList(this.attributes, material.attributes, AttributeHelper.multiplyModifiers(this.extract.primaryAttributes, (double) material.primary), AttributeHelper.multiplyModifiers(this.extract.secondaryAttributes, (double)material.secondary), AttributeHelper.multiplyModifiers(this.extract.tertiaryAttributes, (double)material.tertiary))));

        result.durability = Math.round((float) this.durability + Optional.ofNullable(this.extract.durability).map((extracted) -> extracted * material.durability).orElse(0.0F));

        result.durabilityMultiplier = this.durabilityMultiplier + Optional.ofNullable(this.extract.durabilityMultiplier).map((extracted) -> extracted * material.durability).orElse(0.0F);

        result.integrity = this.integrity + Optional.ofNullable(this.extract.integrity).map((extracted) -> extracted * (extracted > 0.0F ? material.integrityGain : material.integrityCost)).map(Math::round).orElse(0);

        result.magicCapacity = Math.round((float) this.magicCapacity + Optional.ofNullable(this.extract.magicCapacity).map((extracted) -> extracted * (float) material.magicCapacity).orElse(0.0F));

        result.effects = EffectData.merge(Arrays.asList(this.effects, material.effects, EffectData.multiply(this.extract.primaryEffects, material.primary), EffectData.multiply(this.extract.secondaryEffects, material.secondary), EffectData.multiply(this.extract.tertiaryEffects, material.tertiary)));

        result.armor = ArmorData.merge(this.armor, ArmorData.multiply(this.extract.armor, (float) material.armorDefense, material.armorToughness, material.armorKbResistance));

        result.aspects = AspectData.merge(this.aspects, material.aspects);

        if (material.rarity == null || this.rarity != null && material.rarity.ordinal() <= this.rarity.ordinal()) {
            result.rarity = this.rarity;
        } else {
            result.rarity = material.rarity;
        }

        result.glyph = Optional.ofNullable(this.extract.glyph).map((glyph) -> new GlyphData(glyph.textureLocation, glyph.textureX, glyph.textureY, material.tints.glyph)).orElse(this.glyph);
        List<String> availableTextures = Arrays.asList(this.extract.availableTextures);
        result.models =  Stream.concat(Arrays.stream(this.models), Arrays.stream(this.extract.models).map((model) -> MaterialData.kneadModel(model, material, availableTextures))).toArray((x$0) -> new ModuleModel[x$0]);
        if (this.tags == null) {
            result.tags = material.tags;
        } else if (material.tags == null) {
            result.tags = this.tags;
        } else {
            result.tags = Stream.concat(this.tags.stream(), material.tags.stream()).collect(Collectors.toSet());
        }

        return result;
    }
}
