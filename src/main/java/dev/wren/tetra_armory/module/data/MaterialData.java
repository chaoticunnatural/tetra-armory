package dev.wren.tetra_armory.module.data;

import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.TierSortingRegistry;
import se.mickelus.tetra.data.deserializer.AttributesDeserializer;
import se.mickelus.tetra.data.deserializer.ItemTagKeyDeserializer;
import se.mickelus.tetra.module.data.MaterialColors;
import se.mickelus.tetra.module.data.ModuleModel;
import se.mickelus.tetra.module.data.ToolData;
import se.mickelus.tetra.module.schematic.OutcomeMaterial;
import se.mickelus.tetra.properties.AttributeHelper;
import se.mickelus.tetra.util.TierHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class MaterialData {
    private static final MaterialData defaultValues = new MaterialData();
    public boolean replace = false;
    public String key;
    public String category = "misc";
    public boolean hidden = false;
    public boolean hiddenOutcomes = false;
    public Multimap<Attribute, AttributeModifier> attributes;
    public Float primary;
    public Float secondary;
    public Float tertiary;
    public float durability = 0.0F;
    public float integrityGain = 0.0F;
    public float integrityCost = 0.0F;
    public int magicCapacity = 0;
    public EffectData effects = new EffectData();
    public AspectData aspects = new AspectData();
    public int armorDefense = 0;
    public float armorToughness = 0;
    public float armorKbResistance = 0;
    public MaterialColors tints;
    public String[] textures = new String[0];
    public String[] textureOverrides = new String[0];
    public boolean tintOverrides = false;
    public OutcomeMaterial material;
    public ToolData requiredTools;
    public float experienceCost;
    public Set<TagKey<Item>> tags;
    public Rarity rarity;
    public String[] features = new String[0];
    public Map<String, Integer> improvements = new HashMap();

    public static void copyFields(MaterialData from, MaterialData to) {
        if (from.key != null) {
            to.key = from.key;
        }

        if (from.hidden != defaultValues.hidden) {
            to.hidden = from.hidden;
        }

        if (from.hiddenOutcomes != defaultValues.hiddenOutcomes) {
            to.hiddenOutcomes = from.hiddenOutcomes;
        }

        if (!defaultValues.category.equals(from.category)) {
            to.category = from.category;
        }

        if (from.primary != null) {
            to.primary = from.primary;
        }

        if (from.secondary != null) {
            to.secondary = from.secondary;
        }

        if (from.tertiary != null) {
            to.tertiary = from.tertiary;
        }

        if (from.durability != defaultValues.durability) {
            to.durability = from.durability;
        }

        if (from.integrityGain != defaultValues.integrityGain) {
            to.integrityGain = from.integrityGain;
        }

        if (from.integrityCost != defaultValues.integrityCost) {
            to.integrityCost = from.integrityCost;
        }

        if (from.magicCapacity != defaultValues.magicCapacity) {
            to.magicCapacity = from.magicCapacity;
        }

        if (from.armorDefense != defaultValues.armorDefense) {
            to.armorDefense = from.armorDefense;
        }

        if (from.armorToughness != defaultValues.armorToughness) {
            to.armorToughness = from.armorToughness;
        }

        if (from.armorKbResistance != defaultValues.armorKbResistance) {
            to.armorKbResistance = from.armorKbResistance;
        }

        if (from.tints != null) {
            to.tints = from.tints;
        }

        if (from.tintOverrides != defaultValues.tintOverrides) {
            to.tintOverrides = from.tintOverrides;
        }

        to.textureOverrides = Stream.concat(Arrays.stream(to.textureOverrides), Arrays.stream(from.textureOverrides)).distinct().toArray(String[]::new);
        to.attributes = AttributeHelper.overwrite(to.attributes, from.attributes);
        to.effects = EffectData.overwrite(to.effects, from.effects);
        to.requiredTools = ToolData.overwrite(to.requiredTools, from.requiredTools);
        if (from.experienceCost != defaultValues.experienceCost) {
            to.experienceCost = from.experienceCost;
        }

        if (from.material != null) {
            to.material = from.material;
        }

        to.textures = Stream.concat(Arrays.stream(to.textures), Arrays.stream(from.textures)).distinct().toArray(String[]::new);
        if (from.improvements != null) {
            if (to.improvements != null) {
                Map<String, Integer> merged = new HashMap<>();
                merged.putAll(to.improvements);
                merged.putAll(from.improvements);
                to.improvements = merged;
            } else {
                to.improvements = from.improvements;
            }
        }

        if (from.tags != null && to.tags != null) {
            to.tags = Stream.concat(from.tags.stream(), to.tags.stream()).collect(Collectors.toSet());
        } else if (from.tags != null) {
            to.tags = from.tags;
        }

        if (from.rarity != null) {
            to.rarity = from.rarity;
        }

        to.features = Stream.concat(Arrays.stream(to.features), Arrays.stream(from.features)).distinct().toArray(String[]::new);
    }

    public static ModuleModel kneadModel(ModuleModel model, MaterialData material, List<String> availableTextures) {
        if (Arrays.stream(material.textureOverrides).anyMatch((override) -> model.location.getPath().equals(override))) {
            ModuleModel copy = model.copy();
            copy.location = appendString(model.location, material.textures[0]);
            copy.tint = material.tintOverrides ? material.tints.texture : 16777215;
            copy.overlayTint = material.tints.texture;
            return copy;
        } else {
            Stream<String> var10000 = Arrays.stream(material.textures);
            Objects.requireNonNull(availableTextures);
            ResourceLocation updatedLocation = var10000.filter(availableTextures::contains).findFirst().map((texture) -> appendString(model.location, texture)).orElseGet(() -> appendString(model.location, (String)availableTextures.get(0)));
            ModuleModel copy = model.copy();
            copy.location = updatedLocation;
            copy.tint = material.tints.texture;
            copy.overlayTint = material.tints.texture;
            return copy;
        }
    }

    public static ResourceLocation appendString(ResourceLocation resourceLocation, String string) {
        String namespace = resourceLocation.getNamespace();
        String path = resourceLocation.getPath();
        return ResourceLocation.fromNamespaceAndPath(namespace, path + string);
    }

    public MaterialData shallowCopy() {
        MaterialData copy = new MaterialData();
        copyFields(this, copy);
        return copy;
    }

    public static class Deserializer implements JsonDeserializer<MaterialData> {
        private static int getLevel(JsonElement element) {
            return element.getAsJsonPrimitive().isNumber() ? element.getAsInt() : Optional.ofNullable(TierSortingRegistry.byName(ResourceLocation.parse(element.getAsString()))).map(TierHelper::getIndex).map((index) -> index + 1).orElse(0);
        }

        public MaterialData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            MaterialData data = new MaterialData();
            if (jsonObject.has("replace")) {
                data.replace = jsonObject.get("replace").getAsBoolean();
            }

            if (jsonObject.has("key")) {
                data.key = jsonObject.get("key").getAsString();
            }

            if (jsonObject.has("category")) {
                data.category = jsonObject.get("category").getAsString();
            }

            if (jsonObject.has("hidden")) {
                data.hidden = jsonObject.get("hidden").getAsBoolean();
            }

            if (jsonObject.has("hiddenOutcomes")) {
                data.hiddenOutcomes = jsonObject.get("hiddenOutcomes").getAsBoolean();
            }

            if (jsonObject.has("attributes")) {
                data.attributes = context.deserialize(jsonObject.get("attributes"), AttributesDeserializer.typeToken.getRawType());
            }

            if (jsonObject.has("primary")) {
                data.primary = jsonObject.get("primary").getAsFloat();
            }

            if (jsonObject.has("secondary")) {
                data.secondary = jsonObject.get("secondary").getAsFloat();
            }

            if (jsonObject.has("tertiary")) {
                data.tertiary = jsonObject.get("tertiary").getAsFloat();
            }

            if (jsonObject.has("durability")) {
                data.durability = jsonObject.get("durability").getAsFloat();
            }

            if (jsonObject.has("integrityGain")) {
                data.integrityGain = jsonObject.get("integrityGain").getAsFloat();
            }

            if (jsonObject.has("integrityCost")) {
                data.integrityCost = jsonObject.get("integrityCost").getAsFloat();
            }

            if (jsonObject.has("magicCapacity")) {
                data.magicCapacity = jsonObject.get("magicCapacity").getAsInt();
            }

            if (jsonObject.has("effects")) {
                data.effects = context.deserialize(jsonObject.get("effects"), EffectData.class);
            }

            if (jsonObject.has("aspects")) {
                data.aspects = context.deserialize(jsonObject.get("aspects"), AspectData.class);
            }

            if (jsonObject.has("armorDefense")) {
                data.armorDefense = getLevel(jsonObject.get("armorDefense"));
            }

            if (jsonObject.has("armorToughness")) {
                data.armorToughness = jsonObject.get("armorToughness").getAsFloat();
            }

            if (jsonObject.has("armorKbResistance")) {
                data.armorKbResistance = jsonObject.get("armorKbResistance").getAsFloat();
            }

            if (jsonObject.has("tints")) {
                data.tints = context.deserialize(jsonObject.get("tints"), MaterialColors.class);
            }

            if (jsonObject.has("textures")) {
                data.textures = context.deserialize(jsonObject.get("textures"), String[].class);
            }

            if (jsonObject.has("tintOverrides")) {
                data.tintOverrides = jsonObject.get("tintOverrides").getAsBoolean();
            }

            if (jsonObject.has("textureOverrides")) {
                data.textureOverrides = context.deserialize(jsonObject.get("textureOverrides"), String[].class);
            }

            if (jsonObject.has("material")) {
                data.material = context.deserialize(jsonObject.get("material"), OutcomeMaterial.class);
            }

            if (jsonObject.has("requiredTools")) {
                data.requiredTools = context.deserialize(jsonObject.get("requiredTools"), ToolData.class);
            }

            if (jsonObject.has("experienceCost")) {
                data.experienceCost = jsonObject.get("experienceCost").getAsFloat();
            }

            if (jsonObject.has("improvements")) {
                JsonElement improvementsJson = jsonObject.get("improvements");
                if (improvementsJson.isJsonObject()) {
                    data.improvements = improvementsJson.getAsJsonObject().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> ((JsonElement)e.getValue()).getAsInt()));
                }
            }

            if (jsonObject.has("tags")) {
                data.tags = context.deserialize(jsonObject.get("tags"), ItemTagKeyDeserializer.typeToken.getRawType());
            }

            if (jsonObject.has("rarity")) {
                data.rarity = context.deserialize(jsonObject.get("rarity"), Rarity.class);
            }

            if (jsonObject.has("features")) {
                data.features = context.deserialize(jsonObject.get("features"), String[].class);
            }

            return data;
        }
    }
}
