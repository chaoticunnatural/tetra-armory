package dev.wren.tetra_armory.module.data;

import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import se.mickelus.tetra.module.Priority;
import se.mickelus.tetra.module.data.GlyphData;
import se.mickelus.tetra.module.data.ItemProperties;
import se.mickelus.tetra.module.data.ModuleModel;
import se.mickelus.tetra.properties.AttributeHelper;

import java.lang.reflect.Type;
import java.util.Objects;

public class VariantData extends ItemProperties {

    private static final VariantData defaultValues = new VariantData();
    public boolean replace = false;
    public String key;
    public String category = "misc";
    public Multimap<Attribute, AttributeModifier> attributes;
    public ArmorData armor;
    public EffectData effects;
    public AspectData aspects;
    public Priority namePriority;
    public Priority prefixPriority;
    public GlyphData glyph;
    public ModuleModel[] models;
    public int magicCapacity;

    public VariantData() {
        this.namePriority = Priority.BASE;
        this.prefixPriority = Priority.BASE;
        this.glyph = new GlyphData();
        this.models = new ModuleModel[0];
        this.magicCapacity = 0;
    }

    public static VariantData merge(VariantData a, VariantData b) {
        if (b.replace) {
            return b;
        } else {
            VariantData result = new VariantData();
            result.durability = !Objects.equals(b.durability, defaultValues.durability) ? b.durability : a.durability;
            result.durabilityMultiplier = !Objects.equals(b.durabilityMultiplier, defaultValues.durabilityMultiplier) ? b.durabilityMultiplier : a.durabilityMultiplier;
            result.integrity = !Objects.equals(b.integrity, defaultValues.integrity) ? b.integrity : a.integrity;
            result.integrityMultiplier = !Objects.equals(b.integrityMultiplier, defaultValues.integrityMultiplier) ? b.integrityMultiplier : a.integrityMultiplier;
            result.key = !Objects.equals(b.key, defaultValues.key) ? b.key : a.key;
            result.category = !Objects.equals(b.category, defaultValues.category) ? b.category : a.category;
            result.attributes = AttributeHelper.overwrite(a.attributes, b.attributes);
            result.armor = ArmorData.overwrite(a.armor, b.armor);
            result.effects = EffectData.overwrite(a.effects, b.effects);
            result.aspects = AspectData.overwrite(a.aspects, b.aspects);
            result.namePriority = b.namePriority != defaultValues.namePriority ? b.namePriority : a.namePriority;
            result.prefixPriority = b.prefixPriority != defaultValues.prefixPriority ? b.prefixPriority : a.prefixPriority;
            result.glyph = !b.glyph.equals(defaultValues.glyph) ? b.glyph : a.glyph;
            result.models = b.models.length != 0 ? b.models : a.models;
            result.magicCapacity = b.magicCapacity != defaultValues.magicCapacity ? b.magicCapacity : a.magicCapacity;
            return result;
        }
    }

    public static class Deserializer implements JsonDeserializer<VariantData> {
        public VariantData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return jsonObject.has("materials") ? (VariantData)context.deserialize(json, MaterialVariantData.class) : (VariantData)context.deserialize(json, UniqueVariantData.class);
        }
    }
}
