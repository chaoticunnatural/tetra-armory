package dev.wren.tetra_armory.module.data;

import se.mickelus.tetra.aspect.ItemAspect;

import com.google.gson.*;

import java.util.Collection;
import java.lang.reflect.Type;

public class AspectData extends ArmorTierData<ItemAspect> {

    public static AspectData overwrite(AspectData a, AspectData b) {
        return defaultOverwrite(a, b, new AspectData());
    }

    public static AspectData merge(Collection<AspectData> data) {
        return data.stream().reduce(null, AspectData::merge);
    }

    public static AspectData merge(AspectData a, AspectData b) {
        return defaultMerge(a, b, new AspectData());
    }

    public static AspectData multiply(AspectData armorAspectData, float defenseMultiplier, float toughnessMultiplier, float kbResModifier) {
        return defaultMultiply(defenseMultiplier, toughnessMultiplier, kbResModifier, armorAspectData, new AspectData());
    }

    public static class Deserializer implements JsonDeserializer<AspectData> {

        @Override
        public AspectData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            AspectData data = new AspectData();

            jsonObject.entrySet().forEach(entry -> {
                JsonElement entryValue = entry.getValue();
                ItemAspect aspect = ItemAspect.get(entry.getKey());
                if (entryValue.isJsonArray()) {
                    JsonArray entryArray = entryValue.getAsJsonArray();
                    if (entryArray.size() == 3) {
                        data.defenseMap.put(aspect, entryArray.get(0).getAsFloat());
                        data.toughnessMap.put(aspect, entryArray.get(1).getAsFloat());
                        data.kbResMap.put(aspect, entryArray.get(2).getAsFloat());
                    }
                } else {
                    data.defenseMap.put(aspect, entryValue.getAsFloat());
                }
            });

            return data;
        }
    }

}
