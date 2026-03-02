package dev.wren.tetra_armory.module.data;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ImprovementData extends VariantData {

    public int level = 0;
    public boolean enchantment = false;
    public String group = null;

    public int getLevel() {
        return this.level;
    }

    public static class Deserializer implements JsonDeserializer<ImprovementData> {
        public ImprovementData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return jsonObject.has("materials") ? (ImprovementData) context.deserialize(json, MaterialImprovementData.class) : (ImprovementData)context.deserialize(json, UniqueImprovementData.class);
        }
    }

}
