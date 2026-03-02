package dev.wren.tetra_armory.effect.data;

import dev.wren.tetra_armory.effect.ArmorEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import se.mickelus.tetra.effect.data.ItemEffectContext;
import se.mickelus.tetra.effect.data.ItemEffectTrigger;
import se.mickelus.tetra.effect.data.condition.ItemEffectCondition;
import se.mickelus.tetra.effect.data.outcome.ItemEffectOutcome;
import se.mickelus.tetra.effect.data.provider.entity.EntityProvider;
import se.mickelus.tetra.effect.data.provider.number.NumberProvider;
import se.mickelus.tetra.effect.data.provider.vector.VectorProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ArmorEffectData {

    public ArmorEffect effect;
    Data data;
    public ItemEffectTrigger trigger;
    public ItemEffectCondition condition;
    public ItemEffectOutcome outcome;

    public static Map<String, Float> calculateNumbers(Data dataProviders, ItemEffectContext context) {
        return dataProviders != null && dataProviders.numbers != null ? calculateNumbers(dataProviders.numbers, context) : Collections.emptyMap();
    }

    public static Map<String, Float> calculateNumbers(Map<String, NumberProvider> data, ItemEffectContext context) {
        Map<String, Float> result = new HashMap();
        ItemEffectContext updatedContext = context;

        for(Map.Entry<String, NumberProvider> entry : data.entrySet()) {
            result.put(entry.getKey(), (entry.getValue()).getValue(updatedContext));
            updatedContext = updatedContext.withNumbers(result);
        }

        return result;
    }

    public static Map<String, Vec3> calculateVectors(Data dataProviders, ItemEffectContext context) {
        return dataProviders != null && dataProviders.vectors != null ? calculateVectors(dataProviders.vectors, context) : Collections.emptyMap();
    }

    public static Map<String, Vec3> calculateVectors(Map<String, VectorProvider> data, ItemEffectContext context) {
        Map<String, Vec3> result = new HashMap();
        ItemEffectContext updatedContext = context;

        for(Map.Entry<String, VectorProvider> entry : data.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getVector(updatedContext));
            updatedContext = updatedContext.withVectors(result);
        }

        return result;
    }

    public static Map<String, Entity> calculateEntities(Data dataProviders, ItemEffectContext context) {
        return dataProviders != null && dataProviders.entities != null ? calculateEntities(dataProviders.entities, context) : Collections.emptyMap();
    }

    public static Map<String, Entity> calculateEntities(Map<String, EntityProvider> data, ItemEffectContext context) {
        Map<String, Entity> result = new HashMap();
        ItemEffectContext updatedContext = context;

        for(Map.Entry<String, EntityProvider> entry : data.entrySet()) {
            result.put(entry.getKey(), (entry.getValue()).getEntity(updatedContext));
            updatedContext = updatedContext.withEntities(result);
        }

        return result;
    }

    public record Data(Map<String, NumberProvider> numbers, Map<String, VectorProvider> vectors, Map<String, EntityProvider> entities) {}
}
