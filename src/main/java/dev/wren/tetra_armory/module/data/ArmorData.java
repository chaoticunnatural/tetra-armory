package dev.wren.tetra_armory.module.data;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.*;
import dev.wren.tetra_armory.armor.ArmorHitStat;

import java.lang.reflect.Type;

public class ArmorData extends ArmorTierData<ArmorHitStat> {

    public static ArmorData overwrite(ArmorData a, ArmorData b) {
        return defaultOverwrite(a, b, new ArmorData());
    }

    public static ArmorData merge(Collection<ArmorData> data) {
        return data.stream().reduce(null, ArmorData::merge);
    }

    public static ArmorData merge(ArmorData a, ArmorData b) {
        return defaultMerge(a, b, new ArmorData());
    }

    public static ArmorData multiply(ArmorData armorData, float multiplier) {
        return defaultMultiply(multiplier, multiplier, multiplier, armorData, new ArmorData());
    }

    public static ArmorData multiply(ArmorData armorData, float defenseMultiplier, float toughnessMultiplier, float kbResModifier) {
        return defaultMultiply(defenseMultiplier, toughnessMultiplier, kbResModifier, armorData, new ArmorData());
    }

    public static ArmorData offsetLevel(ArmorData armorData, float multiplier, int offset) {
        return Optional.ofNullable(armorData)
                .map(data -> {
                    ArmorData result = new ArmorData();
                    result.defenseMap = data.defenseMap.entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() * multiplier + offset));
                    result.toughnessMap = data.toughnessMap;
                    return result;
                })
                .orElse(null);
    }

    public static ArmorData retainMax(Collection<ArmorData> dataCollection) {
        ArmorData result = new ArmorData();

        dataCollection.forEach(data -> data.getValues().forEach(armorMaterial -> {
            float newLevel = data.defenseMap.getOrDefault(armorMaterial, 0f);
            float currentLevel = result.defenseMap.getOrDefault(armorMaterial, 0f);
            if (newLevel >= result.defenseMap.getOrDefault(armorMaterial, 0f)) {
                result.defenseMap.put(armorMaterial, newLevel);
                if (currentLevel < newLevel) {
                    result.toughnessMap.put(armorMaterial, data.getToughness(armorMaterial));
                } else if (data.getToughness(armorMaterial) > result.getToughness(armorMaterial)) {
                    result.toughnessMap.put(armorMaterial, data.getToughness(armorMaterial));
                }
            }
        }));

        return result;
    }

    public static class Deserializer implements JsonDeserializer<ArmorData> {

        @Override
        public ArmorData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ArmorData data = new ArmorData();

            jsonObject.entrySet().forEach(entry -> {
                JsonElement entryValue = entry.getValue();
                ArmorHitStat armorHitStat = ArmorHitStat.get(entry.getKey());
                if (entryValue.isJsonArray()) {
                    JsonArray entryArray = entryValue.getAsJsonArray();
                    if (entryArray.size() == 3) {
                        data.defenseMap.put(armorHitStat, entryArray.get(0).getAsFloat());
                        data.toughnessMap.put(armorHitStat, entryArray.get(1).getAsFloat());
                        data.kbResMap.put(armorHitStat, entryArray.get(2).getAsFloat());
                    }
                } else {
                    data.defenseMap.put(armorHitStat, entryValue.getAsFloat());
                }
            });

            return data;
        }
    }

}
