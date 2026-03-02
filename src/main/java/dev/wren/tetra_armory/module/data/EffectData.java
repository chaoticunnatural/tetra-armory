package dev.wren.tetra_armory.module.data;

import dev.wren.tetra_armory.effect.ArmorEffect;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Collection;

public class EffectData extends ArmorTierData<ArmorEffect> {

    public static EffectData overwrite(EffectData a, EffectData b) {
        return defaultOverwrite(a, b, new EffectData());
    }

    public static EffectData merge(Collection<EffectData> data) {
        return data.stream().reduce(null, EffectData::merge);
    }

    public static EffectData merge(EffectData a, EffectData b) {
        return defaultMerge(a, b, new EffectData());
    }

    public static EffectData multiply(EffectData effectData, float multiplier) {
        return defaultMultiply(multiplier, multiplier, multiplier, effectData, new EffectData());
    }

    public static EffectData multiply(EffectData effectData, float defenseMultiplier, float toughnessModifier, float kbResModifier) {
        return defaultMultiply(defenseMultiplier, toughnessModifier, kbResModifier, effectData, new EffectData());
    }

    public static class Deserializer implements JsonDeserializer<EffectData> {

        @Override
        public EffectData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            EffectData data = new EffectData();

            jsonObject.entrySet().forEach(entry -> {
                JsonElement entryValue = entry.getValue();
                ArmorEffect effect = ArmorEffect.get(entry.getKey());
                if (entryValue.isJsonArray()) {
                    JsonArray entryArray = entryValue.getAsJsonArray();
                    if (entryArray.size() == 3) {
                        data.defenseMap.put(effect, entryArray.get(0).getAsFloat());
                        data.toughnessMap.put(effect, entryArray.get(1).getAsFloat());
                        data.kbResMap.put(effect, entryArray.get(2).getAsFloat());
                    }
                } else {
                    data.defenseMap.put(effect, entryValue.getAsFloat());
                }
            });

            return data;
        }
    }

}
