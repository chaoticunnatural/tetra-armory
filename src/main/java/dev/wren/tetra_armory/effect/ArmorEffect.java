package dev.wren.tetra_armory.effect;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import se.mickelus.tetra.effect.ItemEffect;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class ArmorEffect {
    private static final Map<String, ArmorEffect> effectMap = new ConcurrentHashMap<>();

    public static final ArmorEffect workable = get("workable");
    public static final ArmorEffect unstable = get("unstable");
    public static final ArmorEffect unbreaking = get("unbreaking");

    private final String key;

    private ArmorEffect(String key) {
        this.key = key;
    }

    public static ArmorEffect get(String key) {
        return (ArmorEffect) effectMap.computeIfAbsent(key, k -> new ArmorEffect(key));
    }

    public String getKey() {
        return key;
    }

    public static class Deserializer implements JsonDeserializer<ArmorEffect> {
        public ArmorEffect deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return ArmorEffect.get(jsonElement.getAsString());
        }
    }
}
